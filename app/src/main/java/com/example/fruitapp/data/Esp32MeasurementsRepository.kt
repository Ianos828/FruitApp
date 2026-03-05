package com.example.fruitapp.data

import com.example.fruitapp.model.Esp32Measurement
import com.example.fruitapp.network.Esp32MeasurementApiService

interface Esp32MeasurementsRepository {
    suspend fun getMeasurements(): Esp32Measurement
}

class NetworkEsp32MeasurementsRepository(
    private val esp32MeasurementApiService: Esp32MeasurementApiService
) : Esp32MeasurementsRepository {
    override suspend fun getMeasurements(): Esp32Measurement = esp32MeasurementApiService.getMeasurements()
}