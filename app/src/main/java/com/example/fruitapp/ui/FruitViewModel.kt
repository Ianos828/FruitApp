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
import com.example.fruitapp.data.Esp32MeasurementsRepository
import com.example.fruitapp.data.ReganMeasurementsRepository
import com.example.fruitapp.model.Measurement
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

/**
 * View Model for the Fruit App
 */
class FruitViewModel (
    private val esp32MeasurementsRepository: Esp32MeasurementsRepository,
    private val reganMeasurementsRepository: ReganMeasurementsRepository
): ViewModel() {

    var fruitUiState: FruitUiState by mutableStateOf(FruitUiState.Loading)
        private set

    init {
        getMeasurement()
    }

    fun getMeasurement() {
        viewModelScope.launch {
            val currentMeasurements = (fruitUiState as? FruitUiState.Success)?.measurements ?: listOf()
            fruitUiState = FruitUiState.Loading
            try {
                val measurement = Measurement(
                    esp32MeasurementsRepository.getMeasurements(),
                    reganMeasurementsRepository.getMeasurements(),
                    currentMeasurements.size + 1
                )
                fruitUiState = FruitUiState.Success(
                    measurement = measurement,
                    measurements = currentMeasurements
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
        val currentState = fruitUiState
        if (currentState is FruitUiState.Success) {
            fruitUiState = currentState.copy(
                measurements = currentState.measurements + currentState.measurement
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as FruitAppApplication)
                val esp32MeasurementsRepository = application.container.esp32MeasurementsRepository
                val reganMeasurementsRepository = application.container.reganMeasurementsRepository
                FruitViewModel(
                    esp32MeasurementsRepository = esp32MeasurementsRepository,
                    reganMeasurementsRepository = reganMeasurementsRepository
                )
            }
        }
    }
}
