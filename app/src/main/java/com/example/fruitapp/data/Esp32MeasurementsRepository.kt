package com.example.fruitapp.data

import com.example.fruitapp.model.Esp32Measurement
import com.example.fruitapp.network.Esp32MeasurementApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

interface Esp32MeasurementsRepository {
    suspend fun getMeasurements(): Esp32Measurement
}

class NetworkEsp32MeasurementsRepository(
    private val esp32MeasurementApiService: Esp32MeasurementApiService,
    private val userPreferencesRepository: UserPreferencesRepository
) : Esp32MeasurementsRepository {

    override suspend fun getMeasurements(): Esp32Measurement = withContext(Dispatchers.IO) {
        try {
            val ip = userPreferencesRepository.esp32Ip.first()
            val url = "http://$ip/trigger"
            esp32MeasurementApiService.getMeasurements(url)
        } catch (e: Exception) {
            e.printStackTrace()
            Esp32Measurement()
        }
    }
}
