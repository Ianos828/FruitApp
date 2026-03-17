package com.example.fruitapp.data

import com.example.fruitapp.model.PressureMeasurement
import com.example.fruitapp.network.PressureMeasurementApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository that provides pressure measurements from the sensor.
 */
interface PressureMeasurementsRepository {
    /**
     * Retrieves the latest pressure measurement from the sensor.
     */
    suspend fun getMeasurements(): PressureMeasurement
}

/**
 * Network implementation of [PressureMeasurementsRepository].
 */
class NetworkPressureMeasurementsRepository(
    private val pressureMeasurementApiService: PressureMeasurementApiService
) : PressureMeasurementsRepository {

    /**
     * Fetches the pressure measurement from the API and returns a [PressureMeasurement].
     * Returns a default [PressureMeasurement] if the request fails.
     */
    override suspend fun getMeasurements(): PressureMeasurement = withContext(Dispatchers.IO) {
        try {
            pressureMeasurementApiService.getMeasurements()
        } catch (e: Exception) {
            e.printStackTrace()
            PressureMeasurement()
        }
    }
}
