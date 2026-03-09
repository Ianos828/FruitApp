package com.example.fruitapp.data

import android.content.Context
import android.graphics.Bitmap
import com.example.fruitapp.model.Measurement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class OfflineMeasurementsRepository(
    private val measurementDao: MeasurementDao,
    private val context: Context
) : MeasurementsRepository {
    override fun getAllMeasurementsStream(): Flow<List<Measurement>> =
        measurementDao.getAllMeasurements()

    override fun getMeasurementStream(id: Int): Flow<Measurement?> =
        measurementDao.getMeasurement(id)

    override suspend fun insertMeasurement(measurement: Measurement) =
        measurementDao.insert(measurement)

    override suspend fun deleteMeasurement(measurement: Measurement) =
        measurementDao.delete(measurement)

    override suspend fun saveImageToInternalStorage(bitmap: Bitmap): String? = withContext(Dispatchers.IO) {
        val fileName = "image_${UUID.randomUUID()}.png"
        val file = File(context.filesDir, fileName)
        try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun deleteAllMeasurements() {
        measurementDao.deleteAll()
        measurementDao.resetId()
    }
}
