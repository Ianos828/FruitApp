package com.example.fruitapp.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fruitapp.ui.SettingsViewModel

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

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
            value = uiState.esp32Ip,
            onValueChange = { viewModel.updateEsp32Ip(it) },
            label = { Text("Hostname/IP") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
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
            value = uiState.pressureSensorIp,
            onValueChange = { viewModel.updatePressureSensorIp(it) },
            label = { Text("Hostname/IP") },
            modifier = Modifier.fillMaxWidth(), singleLine = true
        )
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
            value = uiState.esp32CamIp,
            onValueChange = { viewModel.updateEsp32CamIp(it) },
            label = { Text("Hostname/IP") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Text(
            text = "Camera unit for visual inspection.",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Changes are saved automatically. Use .local addresses or static IPs.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}
