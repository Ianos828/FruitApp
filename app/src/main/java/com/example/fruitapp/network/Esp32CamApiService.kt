package com.example.fruitapp.network

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Url

/**
 * Retrofit service object for fetching the image from the ESP32 CAM
 */
interface Esp32CamApiService {
    @GET
    suspend fun getImage(@Url url: String): ResponseBody
}
