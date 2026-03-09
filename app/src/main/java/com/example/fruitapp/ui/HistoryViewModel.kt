package com.example.fruitapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fruitapp.FruitAppApplication
import com.example.fruitapp.data.MeasurementsRepository
import com.example.fruitapp.model.Measurement
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel to retrieve all measurements in the Room database.
 */
class HistoryViewModel(private val measurementsRepository: MeasurementsRepository): ViewModel() {
    val historyUiState: StateFlow<HistoryUiState> =
        measurementsRepository.getAllMeasurementsStream().map { HistoryUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HistoryUiState()
            )
    
    fun deleteMeasurement(measurement: Measurement) {
        viewModelScope.launch {
            measurementsRepository.deleteMeasurement(measurement)
        }
    }

    fun deleteAllMeasurements() {
        viewModelScope.launch {
            measurementsRepository.deleteAllMeasurements()
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as FruitAppApplication)
                val measurementsRepository = application.container.measurementsRepository

                HistoryViewModel(
                    measurementsRepository = measurementsRepository
                )
            }
        }
    }
}

/**
 * Ui State for HistoryScreen
 */
data class HistoryUiState(val measurementList: List<Measurement> = listOf()) {
}
