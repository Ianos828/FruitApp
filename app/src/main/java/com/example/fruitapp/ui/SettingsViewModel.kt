package com.example.fruitapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fruitapp.FruitAppApplication
import com.example.fruitapp.data.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * UI state for the Settings screen
 */
data class SettingsUiState(
    val esp32Ip: String = "",
    val pressureSensorIp: String = "",
    val esp32CamIp: String = ""
)

/**
 * ViewModel for the Settings screen
 */
class SettingsViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        userPreferencesRepository.esp32Ip,
        userPreferencesRepository.pressureSensorIp,
        userPreferencesRepository.esp32CamIp
    ) { esp32Ip, pressureSensorIp, esp32CamIp ->
        SettingsUiState(
            esp32Ip = esp32Ip,
            pressureSensorIp = pressureSensorIp,
            esp32CamIp = esp32CamIp
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState()
    )

    fun updateEsp32Ip(ip: String) {
        viewModelScope.launch {
            userPreferencesRepository.saveEsp32Ip(ip)
        }
    }

    fun updatePressureSensorIp(ip: String) {
        viewModelScope.launch {
            userPreferencesRepository.savePressureSensorIp(ip)
        }
    }

    fun updateEsp32CamIp(ip: String) {
        viewModelScope.launch {
            userPreferencesRepository.saveEsp32CamIp(ip)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as FruitAppApplication)
                SettingsViewModel(application.container.userPreferencesRepository)
            }
        }
    }
}
