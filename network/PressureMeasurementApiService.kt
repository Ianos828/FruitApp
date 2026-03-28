package com.example.fruitapp.network

import com.example.fruitapp.model.PressureMeasurement
import retrofit2.http.GET
import retrofit2.http.Url

/**
 * Retrofit service object for creating api calls
 */
interface PressureMeasurementApiService {
    @GET
    suspend fun getMeasurements(@Url url: String): PressureMeasurement
}
