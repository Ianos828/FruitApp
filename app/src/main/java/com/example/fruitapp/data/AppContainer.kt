package com.example.fruitapp.data

import android.content.Context
import com.example.fruitapp.network.Esp32CamApiService
import retrofit2.Retrofit
import com.example.fruitapp.network.Esp32MeasurementApiService
import com.example.fruitapp.network.PressureMeasurementApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType

/**
 * Dependency Injection container at the application level.
 */
interface AppContainer {
    val esp32MeasurementsRepository: Esp32MeasurementsRepository
    val pressureMeasurementsRepository: PressureMeasurementsRepository
    val esp32CamRepository: Esp32CamRepository
    val measurementsRepository: MeasurementsRepository
    val fruitPredictor: FruitPredictor
}

/**
 * [AppContainer] implementation that provides dependencies.
 */
class DefaultAppContainer(private val context: Context): AppContainer {

    /**
     * JSON configuration for serialization.
     */
    private val json = Json {
        ignoreUnknownKeys = true 
    }

    private val esp32BaseUrl = "http://esp32_combined.local/"
    private val pressureBaseUrl = "http://force_sensor.local/"
    private val esp32CamBaseUrl = "http://esp32_cam_image.local/"

    /**
     * Retrofit instance for ESP32 measurement API.
     */
    private val esp32Retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(esp32BaseUrl)
        .build()

    /**
     * Retrofit instance for Pressure sensor API.
     */
    private val pressureRetrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(pressureBaseUrl)
        .build()

    /**
     * Retrofit instance for ESP32 camera API.
     */
    private val esp32CamRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(esp32CamBaseUrl)
        .build()

    /**
     * Lazily initialized ESP32 measurement API service.
     */
    private val esp32RetrofitService: Esp32MeasurementApiService by lazy {
        esp32Retrofit.create(Esp32MeasurementApiService::class.java)
    }

    /**
     * Lazily initialized Pressure measurement API service.
     */
    private val pressureRetrofitService: PressureMeasurementApiService by lazy {
        pressureRetrofit.create(PressureMeasurementApiService::class.java)
    }

    /**
     * Lazily initialized ESP32 camera API service.
     */
    private val esp32CamRetrofitService: Esp32CamApiService by lazy {
        esp32CamRetrofit.create(Esp32CamApiService::class.java)
    }

    override val esp32MeasurementsRepository: Esp32MeasurementsRepository by lazy {
        NetworkEsp32MeasurementsRepository(esp32RetrofitService)
    }

    override val pressureMeasurementsRepository: PressureMeasurementsRepository by lazy {
        NetworkPressureMeasurementsRepository(pressureRetrofitService)
    }

    override val esp32CamRepository: Esp32CamRepository by lazy {
        NetworkEsp32CamRepository(esp32CamRetrofitService, context)
    }

    override val measurementsRepository: MeasurementsRepository by lazy {
        OfflineMeasurementsRepository(
            MeasurementDatabase
            .getDatabase(context)
            .measurementDao(),
            context
        )
    }

    override val fruitPredictor: FruitPredictor by lazy {
        FruitPredictor(context)
    }
}
