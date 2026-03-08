package com.example.fruitapp.model

import android.graphics.Bitmap

/**
 * Data class to represent the image received from the ESP32 CAM
 */
data class Image(
    val bitmap: Bitmap
)
