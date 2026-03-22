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
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
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

    init {
        getMeasurement()
    }

    /**
     * Fetches a new measurement from all sensors AND starts the Lidar scan.
     */
    fun getMeasurement() {
        viewModelScope.launch {
            fruitUiState = FruitUiState.Loading
            
            try {
                val esp32Deferred = async { esp32MeasurementsRepository.getMeasurements() }
                val pressureDeferred = async { pressureMeasurementsRepository.getMeasurements() }
                val imageDeferred = async { esp32CamRepository.getImage() }

                val esp32Result = esp32Deferred.await()
                val pressureResult = pressureDeferred.await()
                val imageResult = imageDeferred.await()

                var prediction: String = "No Prediction"

                if (!esp32Result.isDefault() && !pressureResult.isDefault()) {
                    prediction = fruitPredictor.predict(esp32Result, pressureResult)
                }

                val measurement = Measurement(
                    esp32Measurement = esp32Result,
                    pressureMeasurement = pressureResult,
                    image = imageResult,
                    prediction = prediction,
                    date = LocalDateTime.now()
                )
                fruitUiState = FruitUiState.Success(
                    measurement = measurement,
                    isLidarScanning = true
                )
                
                // Start lidar streaming update
                startLidarScanInternal()
                
            } catch (e: Exception) {
                Log.e("FruitViewModel", "Error fetching measurements", e)
                fruitUiState = FruitUiState.Error
            }
        }
    }

    /**
     * Internal function to handle Lidar streaming logic.
     */
    private fun startLidarScanInternal() {
        viewModelScope.launch {
            val currentPoints = mutableListOf<LidarPoint>()
            
            lidarRepository.getLidarStreaming()
                .catch { e ->
                    Log.e("FruitViewModel", "Error in lidar stream", e)
                    val currentState = fruitUiState
                    if (currentState is FruitUiState.Success) {
                        fruitUiState = currentState.copy(isLidarScanning = false, lidarError = true)
                    }
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
                            measurement = updatedMeasurement,
                            isLidarScanning = true // still scanning
                        )
                    }
                }
            
            // Flow finished successfully
            val finalState = fruitUiState
            if (finalState is FruitUiState.Success) {
                fruitUiState = finalState.copy(isLidarScanning = false)
            }
        }
    }

    /**
     * Saves the current measurement to the history database.
     */
    fun saveCurrentMeasurement() {
        viewModelScope.launch {
            val successState = fruitUiState as? FruitUiState.Success ?: return@launch
            val currentMeasurement = successState.measurement
            val bitmap = currentMeasurement.image.bitmap ?: return@launch

            val filePath = measurementsRepository.saveImageToInternalStorage(bitmap)
            val measurementToSave = currentMeasurement.copy(
                image = currentMeasurement.image.copy(filePath = filePath)
            )
            measurementsRepository.insertMeasurement(measurementToSave)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as FruitAppApplication)
                FruitViewModel(
                    esp32MeasurementsRepository = application.container.esp32MeasurementsRepository,
                    pressureMeasurementsRepository = application.container.pressureMeasurementsRepository,
                    esp32CamRepository = application.container.esp32CamRepository,
                    measurementsRepository = application.container.measurementsRepository,
                    lidarRepository = application.container.lidarRepository,
                    fruitPredictor = application.container.fruitPredictor
                )
            }
        }
    }
}
