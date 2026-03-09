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
import com.example.fruitapp.data.MeasurementsRepository
import com.example.fruitapp.data.ReganMeasurementsRepository
import com.example.fruitapp.model.Measurement
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.time.LocalDateTime

/**
 * View Model for the Fruit App
 */
class FruitViewModel (
    private val esp32MeasurementsRepository: Esp32MeasurementsRepository,
    private val reganMeasurementsRepository: ReganMeasurementsRepository,
    private val esp32CamRepository: Esp32CamRepository,
    private val measurementsRepository: MeasurementsRepository
): ViewModel() {

    var fruitUiState: FruitUiState by mutableStateOf(FruitUiState.Loading)
        private set

    init {
        getMeasurement()
    }

    fun getMeasurement() {
        viewModelScope.launch {
            fruitUiState = FruitUiState.Loading
            try {
                val measurement = Measurement(
                    esp32Measurement = esp32MeasurementsRepository.getMeasurements(),
                    reganMeasurement = reganMeasurementsRepository.getMeasurements(),
                    image = esp32CamRepository.getImage(),
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
     * Saves the current measurement to the history list.
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
                val esp32MeasurementsRepository = application.container.esp32MeasurementsRepository
                val reganMeasurementsRepository = application.container.reganMeasurementsRepository
                val esp32CamRepository = application.container.esp32CamRepository
                val measurementsRepository = application.container.measurementsRepository

                FruitViewModel(
                    esp32MeasurementsRepository = esp32MeasurementsRepository,
                    reganMeasurementsRepository = reganMeasurementsRepository,
                    esp32CamRepository = esp32CamRepository,
                    measurementsRepository = measurementsRepository
                )
            }
        }
    }
}
