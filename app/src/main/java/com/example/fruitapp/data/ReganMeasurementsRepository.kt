package com.example.fruitapp.data

import com.example.fruitapp.model.ReganMeasurement
import com.example.fruitapp.network.ReganMeasurementApiService

interface ReganMeasurementsRepository {
    suspend fun getMeasurements(): ReganMeasurement
}

class NetworkReganMeasurementsRepository(
    private val reganMeasurementApiService: ReganMeasurementApiService
) : ReganMeasurementsRepository {
    override suspend fun getMeasurements(): ReganMeasurement = reganMeasurementApiService.getMeasurements()
}