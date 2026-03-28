package com.example.fruitapp.network

import com.example.fruitapp.model.Esp32Measurement
import retrofit2.http.GET
import retrofit2.http.Url

/**
 * Retrofit service object for creating api calls
 */
interface Esp32MeasurementApiService {
    @GET
    suspend fun getMeasurements(@Url url: String): Esp32Measurement
}
