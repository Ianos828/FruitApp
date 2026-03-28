package com.example.fruitapp.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fruitapp.ui.SettingsViewModel

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)
) {
    val savedSettings by viewModel.savedSettings.collectAsState()
    
    // Ensure inputs are refreshed from saved state whenever the screen is entered
    LaunchedEffect(Unit) {
        viewModel.loadSavedSettings()
    }

    // Intercept system back press
    BackHandler {
        viewModel.onBackRequested(savedSettings, onNavigateBack)
    }

    if (viewModel.showUnsavedDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissUnsavedDialog() },
            title = { Text("Unsaved Changes") },
            text = { Text("You have unsaved hardware settings. Do you want to discard them?") },
            confirmButton = {
                TextButton(onClick = { 
                    viewModel.dismissUnsavedDialog()
                    onNavigateBack()
                }) {
                    Text("Discard")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissUnsavedDialog() }) {
                    Text("Stay")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Hardware Settings",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Combined ESP32 Setting
        Text(
            text = "ESP32 Sensor Unit",
            style = MaterialTheme.typography.titleMedium
        )
        OutlinedTextField(
            value = viewModel.esp32IpInput,
            onValueChange = { viewModel.updateEsp32Ip(it) },
            label = { Text("Hostname/IP") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = viewModel.esp32IpInput.isBlank()
        )
        if (viewModel.esp32IpInput.isBlank()) {
            Text("Address cannot be empty", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Text(
            text = "Main sensor unit (Fluorescence, NIR, Ethylene, etc.)",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        // Pressure Sensor Setting
        Text(
            text = "Pressure Sensor Unit",
            style = MaterialTheme.typography.titleMedium
        )
        OutlinedTextField(
            value = viewModel.pressureSensorIpInput,
            onValueChange = { viewModel.updatePressureSensorIp(it) },
            label = { Text("Hostname/IP") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = viewModel.pressureSensorIpInput.isBlank()
        )
        if (viewModel.pressureSensorIpInput.isBlank()) {
            Text("Address cannot be empty", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Text(
            text = "Force sensor unit",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        // Camera Setting
        Text(
            text = "ESP32 Camera Unit",
            style = MaterialTheme.typography.titleMedium
        )
        OutlinedTextField(
            value = viewModel.esp32CamIpInput,
            onValueChange = { viewModel.updateEsp32CamIp(it) },
            label = { Text("Hostname/IP") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = viewModel.esp32CamIpInput.isBlank()
        )
        if (viewModel.esp32CamIpInput.isBlank()) {
            Text("Address cannot be empty", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Text(
            text = "Camera unit for visual inspection.",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.saveSettings(onSuccess = onNavigateBack) },
            modifier = Modifier.fillMaxWidth(),
            enabled = viewModel.isInputValid()
        ) {
            Text("Save Settings")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { viewModel.resetSettings() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("Reset to Defaults")
        }

        if (viewModel.isSaveSuccessful) {
            Text(
                text = "Settings saved successfully!",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Use .local addresses or static IPs. All fields are required.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}
