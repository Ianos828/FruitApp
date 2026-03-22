package com.example.fruitapp.network

import com.example.fruitapp.model.Esp32Measurement
import retrofit2.http.GET

/**
 * Retrofit service object for creating api calls
 */
interface Esp32MeasurementApiService {
    @GET("trigger")
    suspend fun getMeasurements(): Esp32Measurement
}
