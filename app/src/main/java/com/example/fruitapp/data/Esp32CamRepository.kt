package com.example.fruitapp.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import com.example.fruitapp.R
import com.example.fruitapp.model.Image
import com.example.fruitapp.network.Esp32CamApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import androidx.core.graphics.createBitmap

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
    private val esp32CamApiService: Esp32CamApiService,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val context: Context
) : Esp32CamRepository {
    override suspend fun getImage(): Image = withContext(Dispatchers.IO) {
        try {
            val ip = userPreferencesRepository.esp32CamIp.first()
            val url = "http://$ip/image"
            val responseBody = esp32CamApiService.getImage(url)
            val bytes = responseBody.bytes()
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            Image(bitmap ?: getPlaceholderBitmap())
        } catch (e: Exception) {
            e.printStackTrace()
            Image(getPlaceholderBitmap())
        }
    }

    private fun getPlaceholderBitmap(): Bitmap {
        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_broken_image)
            ?: throw IllegalStateException("Resource not found")
        
        val bitmap = createBitmap(
            drawable.intrinsicWidth.coerceAtLeast(1),
            drawable.intrinsicHeight.coerceAtLeast(1)
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}
