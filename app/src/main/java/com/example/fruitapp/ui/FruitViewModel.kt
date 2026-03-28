package com.example.fruitapp.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fruitapp.FruitAppApplication
import com.example.fruitapp.data.Esp32CamRepository
import com.example.fruitapp.data.Esp32MeasurementsRepository
import com.example.fruitapp.data.FruitPredictor
import com.example.fruitapp.data.LidarRepository
import com.example.fruitapp.data.MeasurementsRepository
import com.example.fruitapp.data.PressureMeasurementsRepository
import com.example.fruitapp.model.LidarPoint
import com.example.fruitapp.model.LidarScan
import com.example.fruitapp.model.Measurement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

/**
 * ViewModel for the Fruit App.
 * Handles fetching live sensor data and saving measurements to history.
 */
class FruitViewModel(
    private val esp32MeasurementsRepository: Esp32MeasurementsRepository,
    private val pressureMeasurementsRepository: PressureMeasurementsRepository,
    private val esp32CamRepository: Esp32CamRepository,
    private val measurementsRepository: MeasurementsRepository,
    private val lidarRepository: LidarRepository,
    private val fruitPredictor: FruitPredictor
) : ViewModel() {

    /**
     * The current UI state of the fruit measurement process.
     */
    var fruitUiState: FruitUiState by mutableStateOf(FruitUiState.Loading)
        private set

    /**
     * Job tracking the current lidar collection to allow cancellation.
     */
    private var lidarCollectionJob: Job? = null

    /**
     * Fetches a new measurement from all sensors AND starts the Lidar scan.
     */
    fun getMeasurement() {
        // Cancel any existing job before starting a new one
        lidarCollectionJob?.cancel()
        
        viewModelScope.launch {
            fruitUiState = FruitUiState.Loading
            
            try {
                // Phase 1: Start sensor fetches concurrently.
                val esp32Deferred = async { 
                    try { esp32MeasurementsRepository.getMeasurements() } catch (e: Exception) { com.example.fruitapp.model.Esp32Measurement() } 
                }
                val pressureDeferred = async { 
                    try { pressureMeasurementsRepository.getMeasurements() } catch (e: Exception) { com.example.fruitapp.model.PressureMeasurement() } 
                }
                val imageDeferred = async { 
                    try { esp32CamRepository.getImage() } catch (e: Exception) { com.example.fruitapp.model.Image() } 
                }

                val esp32Result = esp32Deferred.await()
                val pressureResult = pressureDeferred.await()
                val imageResult = imageDeferred.await()

                var prediction: String = "No Prediction"

                if (!esp32Result.isDefault() && !pressureResult.isDefault()) {
                    // Move ML prediction to a background thread to avoid blocking the UI
                    prediction = withContext(Dispatchers.Default) {
                        fruitPredictor.predict(esp32Result, pressureResult)
                    }
                }

                val measurement = Measurement(
                    esp32Measurement = esp32Result,
                    pressureMeasurement = pressureResult,
                    image = imageResult,
                    prediction = prediction,
                    date = LocalDateTime.now()
                )
                
                // Show Success state with current sensor data immediately
                fruitUiState = FruitUiState.Success(
                    measurement = measurement
                )
                
                // Trigger Lidar WebSocket scan immediately after HTTP results arrive
                startLidarCollection()
                
            } catch (e: Exception) {
                Log.e("FruitViewModel", "Critical error in measurement sequence", e)
                fruitUiState = FruitUiState.Error
            }
        }
    }

    /**
     * Internal logic to handle real-time Lidar collection.
     */
    private fun startLidarCollection() {
        lidarCollectionJob?.cancel()
        lidarCollectionJob = viewModelScope.launch {
            try {
                val lidarStream = lidarRepository.getLidarStreaming()
                val currentPoints = mutableListOf<LidarPoint>()
                
                lidarStream
                    .catch { e ->
                        Log.e("FruitViewModel", "Error in lidar stream", e)
                    }
                    .collect { newPoint ->
                        currentPoints.add(newPoint)
                        val currentState = fruitUiState
                        if (currentState is FruitUiState.Success) {
                            val updatedMeasurement = currentState.measurement.copy(
                                esp32Measurement = currentState.measurement.esp32Measurement.copy(
                                    lidarScan = LidarScan(ArrayList(currentPoints))
                                )
                            )
                            fruitUiState = currentState.copy(
                                measurement = updatedMeasurement
                            )
                        }
                    }
            } catch (e: Exception) {
                Log.e("FruitViewModel", "Could not initiate lidar collection", e)
            }
        }
    }

    /**
     * Sends a stop signal to the Lidar motor and stops the current scan.
     */
    fun stopLidarAndReturnHome() {
        viewModelScope.launch {
            // Send stop command to repository
            lidarRepository.stopLidarScan()
            // Cancel collection job
            lidarCollectionJob?.cancel()
        }
    }

    /**
     * Saves the current measurement to the history database.
     */
    fun saveCurrentMeasurement() {
        viewModelScope.launch {
            val successState = fruitUiState as? FruitUiState.Success ?: return@launch
            val currentMeasurement = successState.measurement
            val bitmap = currentMeasurement.image.bitmap

            try {
                // Perform I/O operations on the IO dispatcher
                withContext(Dispatchers.IO) {
                    val filePath = if (bitmap != null) {
                        measurementsRepository.saveImageToInternalStorage(bitmap)
                    } else {
                        currentMeasurement.image.filePath
                    }

                    val measurementToSave = currentMeasurement.copy(
                        image = currentMeasurement.image.copy(filePath = filePath)
                    )
                    measurementsRepository.insertMeasurement(measurementToSave)
                }
            } catch (e: Exception) {
                Log.e("FruitViewModel", "Error saving measurement", e)
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as FruitAppApplication)
                val esp32MeasurementsRepository = application.container.esp32MeasurementsRepository
                val pressureMeasurementsRepository = application.container.pressureMeasurementsRepository
                val esp32CamRepository = application.container.esp32CamRepository
                val measurementsRepository = application.container.measurementsRepository
                val lidarRepository = application.container.lidarRepository
                val fruitPredictor = application.container.fruitPredictor

                FruitViewModel(
                    esp32MeasurementsRepository = esp32MeasurementsRepository,
                    pressureMeasurementsRepository = pressureMeasurementsRepository,
                    esp32CamRepository = esp32CamRepository,
                    measurementsRepository = measurementsRepository,
                    lidarRepository = lidarRepository,
                    fruitPredictor = fruitPredictor
                )
            }
        }
    }
}
