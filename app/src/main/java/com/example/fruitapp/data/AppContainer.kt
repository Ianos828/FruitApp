package com.example.fruitapp.data

import com.example.fruitapp.network.Esp32CamApiService
import retrofit2.Retrofit
import com.example.fruitapp.network.Esp32MeasurementApiService
import com.example.fruitapp.network.ReganMeasurementApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType

interface AppContainer {
    val esp32MeasurementsRepository: Esp32MeasurementsRepository
    val reganMeasurementsRepository: ReganMeasurementsRepository
    val esp32CamRepository: Esp32CamRepository
}

class DefaultAppContainer : AppContainer {

    // Configure Json to ignore fields it doesn't recognize
    private val json = Json {
        ignoreUnknownKeys = true 
    }

    // Dummy URL that returns valid JSON (Todo #1)
    private val esp32BaseUrl = "http://esp32_combined.local/"
    private val reganBaseUrl = "http://force_sensor.local/"
    private val esp32CamBaseUrl = "http://esp32_cam_image.local/"

    private val esp32Retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(esp32BaseUrl)
        .build()

    private val reganRetrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(reganBaseUrl)
        .build()

    // No JSON converter factory here because we are fetching a raw image
    private val esp32CamRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(esp32CamBaseUrl)
        .build()

    private val esp32RetrofitService: Esp32MeasurementApiService by lazy {
        esp32Retrofit.create(Esp32MeasurementApiService::class.java)
    }

    private val reganRetrofitService: ReganMeasurementApiService by lazy {
        reganRetrofit.create(ReganMeasurementApiService::class.java)
    }

    private val esp32CamRetrofitService: Esp32CamApiService by lazy {
        esp32CamRetrofit.create(Esp32CamApiService::class.java)
    }

    override val esp32MeasurementsRepository: Esp32MeasurementsRepository by lazy {
        NetworkEsp32MeasurementsRepository(esp32RetrofitService)
    }

    override val reganMeasurementsRepository: ReganMeasurementsRepository by lazy {
        NetworkReganMeasurementsRepository(reganRetrofitService)
    }

    override val esp32CamRepository: Esp32CamRepository by lazy {
        NetworkEsp32CamRepository(esp32CamRetrofitService)
    }
}
