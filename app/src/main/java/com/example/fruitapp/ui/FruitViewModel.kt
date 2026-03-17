package com.example.fruitapp.ui

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
import com.example.fruitapp.data.MeasurementsRepository
import com.example.fruitapp.data.PressureMeasurementsRepository
import com.example.fruitapp.model.Measurement
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.time.LocalDateTime

/**
 * ViewModel for the Fruit App.
 * Handles fetching live sensor data and saving measurements to history.
 */
class FruitViewModel (
    private val esp32MeasurementsRepository: Esp32MeasurementsRepository,
    private val pressureMeasurementsRepository: PressureMeasurementsRepository,
    private val esp32CamRepository: Esp32CamRepository,
    private val measurementsRepository: MeasurementsRepository,
    private val fruitPredictor: FruitPredictor
): ViewModel() {

    /**
     * The current UI state of the fruit measurement process.
     */
    var fruitUiState: FruitUiState by mutableStateOf(FruitUiState.Loading)
        private set

    init {
        getMeasurement()
    }

    /**
     * Fetches a new measurement from all sensors.
     * Sensors are queried in parallel where possible, with the camera image
     * being fetched after the initial sensor data is received.
     */
    fun getMeasurement() {
        viewModelScope.launch {
            fruitUiState = FruitUiState.Loading
            try {
                // Start the two measurement requests in parallel
                val esp32Deferred = async { esp32MeasurementsRepository.getMeasurements() }
                val pressureDeferred = async { pressureMeasurementsRepository.getMeasurements() }

                // Wait for the sensors to finish first
                val esp32Result = esp32Deferred.await()
                val pressureResult = pressureDeferred.await()

                // Generate prediction using the ONNX model
                val prediction = fruitPredictor.predict(esp32Result, pressureResult)

                // Now that measurements are done, fetch the image
                val imageResult = esp32CamRepository.getImage()

                val measurement = Measurement(
                    esp32Measurement = esp32Result,
                    pressureMeasurement = pressureResult,
                    image = imageResult,
                    prediction = prediction,
                    date = LocalDateTime.now()
                )
                fruitUiState = FruitUiState.Success(
                    measurement = measurement
                )
            } catch (e: IOException) {
                fruitUiState = FruitUiState.Error
            } catch (e: HttpException) {
                fruitUiState = FruitUiState.Error
            }
        }
    }

    /**
     * Saves the current measurement from the [FruitUiState.Success] state to the history database.
     * This includes saving the bitmap to internal storage and persisting the file path.
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
        /**
         * Factory to create the [FruitViewModel] with required dependencies.
         */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as FruitAppApplication)
                val esp32MeasurementsRepository = application.container.esp32MeasurementsRepository
                val pressureMeasurementsRepository = application.container.pressureMeasurementsRepository
                val esp32CamRepository = application.container.esp32CamRepository
                val measurementsRepository = application.container.measurementsRepository
                val fruitPredictor = application.container.fruitPredictor

                FruitViewModel(
                    esp32MeasurementsRepository = esp32MeasurementsRepository,
                    pressureMeasurementsRepository = pressureMeasurementsRepository,
                    esp32CamRepository = esp32CamRepository,
                    measurementsRepository = measurementsRepository,
                    fruitPredictor = fruitPredictor
                )
            }
        }
    }
}
