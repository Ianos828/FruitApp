package com.example.fruitapp.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.fruitapp.model.Image
import com.example.fruitapp.network.Esp32CamApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for fetching the image from the ESP32 CAM
 */
interface Esp32CamRepository {
    suspend fun getImage(): Image
}

/**
 * Network implementation of the Esp32CamRepository
 */
class NetworkEsp32CamRepository(
    private val esp32CamApiService: Esp32CamApiService
) : Esp32CamRepository {
    override suspend fun getImage(): Image = withContext(Dispatchers.IO) {
        val responseBody = esp32CamApiService.getImage()
        val bytes = responseBody.bytes()
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        Image(bitmap)
    }
}
