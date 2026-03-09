package com.example.fruitapp.data

import android.graphics.Bitmap
import com.example.fruitapp.model.Measurement
import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Measurement] from a given data source.
 */
interface MeasurementsRepository {
    /**
     * Retrieve all the measurements from the given data source.
     */
    fun getAllMeasurementsStream(): Flow<List<Measurement>>

    /**
     * Retrieve a measurement from the given data source that matches with the [id].
     */
    fun getMeasurementStream(id: Int): Flow<Measurement?>

    /**
     * Insert measurement in the data source
     */
    suspend fun insertMeasurement(measurement: Measurement)

    /**
     * Delete measurement from the data source
     */
    suspend fun deleteMeasurement(measurement: Measurement)

    /**
     * Save bitmap to internal storage and return the file path.
     */
    suspend fun saveImageToInternalStorage(bitmap: Bitmap): String?

    /**
     * Clear all measurements and reset the autoincrement ID.
     */
    suspend fun deleteAllMeasurements()
}
