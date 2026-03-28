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
import com.example.fruitapp.data.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * UI state for the Settings screen
 */
data class SettingsUiState(
    val esp32Ip: String = "esp32_combined.local",
    val pressureSensorIp: String = "force_sensor.local",
    val esp32CamIp: String = "esp32_cam_image.local",
    val isEntryValid: Boolean = false,
    val isSaveSuccessful: Boolean = false
)

/**
 * ViewModel for the Settings screen
 */
class SettingsViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val defaultEsp32Ip = "esp32_combined.local"
    private val defaultPressureIp = "force_sensor.local"
    private val defaultCamIp = "esp32_cam_image.local"

    var esp32IpInput by mutableStateOf(defaultEsp32Ip)
        private set
    var pressureSensorIpInput by mutableStateOf(defaultPressureIp)
        private set
    var esp32CamIpInput by mutableStateOf(defaultCamIp)
        private set

    var isSaveSuccessful by mutableStateOf(false)
        private set

    var showUnsavedDialog by mutableStateOf(false)
        private set

    init {
        loadSavedSettings()
    }

    /**
     * Loads the actual saved values from the repository into the input fields.
     */
    fun loadSavedSettings() {
        viewModelScope.launch {
            val initialEsp32Ip = userPreferencesRepository.esp32Ip.first()
            val initialPressureIp = userPreferencesRepository.pressureSensorIp.first()
            val initialCamIp = userPreferencesRepository.esp32CamIp.first()
            
            esp32IpInput = initialEsp32Ip
            pressureSensorIpInput = initialPressureIp
            esp32CamIpInput = initialCamIp
            isSaveSuccessful = false
        }
    }

    fun updateEsp32Ip(ip: String) {
        esp32IpInput = ip
        isSaveSuccessful = false
    }

    fun updatePressureSensorIp(ip: String) {
        pressureSensorIpInput = ip
        isSaveSuccessful = false
    }

    fun updateEsp32CamIp(ip: String) {
        esp32CamIpInput = ip
        isSaveSuccessful = false
    }

    fun isInputValid(): Boolean {
        return esp32IpInput.isNotBlank() && 
               pressureSensorIpInput.isNotBlank() && 
               esp32CamIpInput.isNotBlank()
    }

    fun saveSettings(onSuccess: () -> Unit) {
        if (isInputValid()) {
            viewModelScope.launch {
                userPreferencesRepository.saveEsp32Ip(esp32IpInput)
                userPreferencesRepository.savePressureSensorIp(pressureSensorIpInput)
                userPreferencesRepository.saveEsp32CamIp(esp32CamIpInput)
                isSaveSuccessful = true
                onSuccess()
            }
        }
    }

    /**
     * Resets the input fields to the default hostnames.
     */
    fun resetSettings() {
        esp32IpInput = defaultEsp32Ip
        pressureSensorIpInput = defaultPressureIp
        esp32CamIpInput = defaultCamIp
        isSaveSuccessful = false
    }

    /**
     * Checks if there are unsaved changes.
     */
    fun hasUnsavedChanges(savedEsp32: String, savedPressure: String, savedCam: String): Boolean {
        return (esp32IpInput != savedEsp32 || 
                pressureSensorIpInput != savedPressure || 
                esp32CamIpInput != savedCam) && !isSaveSuccessful
    }

    /**
     * Logic to handle back navigation request.
     */
    fun onBackRequested(savedUiState: SettingsUiState, onDirectBack: () -> Unit) {
        if (!isInputValid()) {
            // Blocked: Do nothing
            return
        }
        
        if (hasUnsavedChanges(savedUiState.esp32Ip, savedUiState.pressureSensorIp, savedUiState.esp32CamIp)) {
            showUnsavedDialog = true
        } else {
            onDirectBack()
        }
    }

    fun dismissUnsavedDialog() {
        showUnsavedDialog = false
    }

    val savedSettings: StateFlow<SettingsUiState> = combine(
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

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as FruitAppApplication)
                SettingsViewModel(application.container.userPreferencesRepository)
            }
        }
    }
}
