package com.example.fruitapp.data

import com.example.fruitapp.model.Esp32Measurement
import com.example.fruitapp.network.Esp32MeasurementApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface Esp32MeasurementsRepository {
    suspend fun getMeasurements(): Esp32Measurement
}

class NetworkEsp32MeasurementsRepository(
    private val esp32MeasurementApiService: Esp32MeasurementApiService
) : Esp32MeasurementsRepository {

    override suspend fun getMeasurements(): Esp32Measurement = withContext(Dispatchers.IO) {
        try {
            esp32MeasurementApiService.getMeasurements()
        } catch (e: Exception) {
            e.printStackTrace()
            // handle error, maybe return default/fallback measurement
            Esp32Measurement() // or null if your class allows
        }
    }
}
