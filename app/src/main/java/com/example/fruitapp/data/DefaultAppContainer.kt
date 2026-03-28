package com.example.fruitapp.data

import android.content.Context
import com.example.fruitapp.network.Esp32CamApiService
import retrofit2.Retrofit
import com.example.fruitapp.network.Esp32MeasurementApiService
import com.example.fruitapp.network.LidarApiService
import com.example.fruitapp.network.PressureMeasurementApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient

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

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context.dataStore)
    }

//    private val esp32BaseUrl = "http://10.197.233.131/"
//    private val pressureBaseUrl = "http://10.197.233.83/"
//    private val esp32CamBaseUrl = "http://10.197.233.186/"

    /**
     * Shared OkHttpClient for network requests and WebSockets.
     */
    private val okHttpClient = OkHttpClient()

    // Base URLs are now dynamic, but we need a valid base URL for Retrofit initialization.
    // We'll use a placeholder and use @Url in the ApiService interfaces.
    private val placeholderBaseUrl = "http://localhost/"

    /**
     * Retrofit instance for ESP32 measurement API.
     */
    private val esp32Retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(placeholderBaseUrl)
        .client(okHttpClient)
        .build()

    /**
     * Retrofit instance for Pressure sensor API.
     */
    private val pressureRetrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(placeholderBaseUrl)
        .client(okHttpClient)
        .build()

    /**
     * Retrofit instance for ESP32 camera API.
     */
    private val esp32CamRetrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(placeholderBaseUrl)
        .client(okHttpClient)
        .build()

    /**
     * Lazily initialized ESP32 measurement API service.
     */
    private val esp32RetrofitService: Esp32MeasurementApiService by lazy {
        esp32Retrofit.create(Esp32MeasurementApiService::class.java)
    }

    /**
     * Lazily initialized Lidar API service.
     */
    private val lidarRetrofitService: LidarApiService by lazy {
        esp32Retrofit.create(LidarApiService::class.java)
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
        NetworkEsp32MeasurementsRepository(esp32RetrofitService, userPreferencesRepository)
    }

    override val pressureMeasurementsRepository: PressureMeasurementsRepository by lazy {
        NetworkPressureMeasurementsRepository(pressureRetrofitService, userPreferencesRepository)
    }

    override val esp32CamRepository: Esp32CamRepository by lazy {
        NetworkEsp32CamRepository(esp32CamRetrofitService, userPreferencesRepository, context)
    }

    override val measurementsRepository: MeasurementsRepository by lazy {
        OfflineMeasurementsRepository(
            MeasurementDatabase
            .getDatabase(context)
            .measurementDao(),
            context
        )
    }

    override val lidarRepository: LidarRepository by lazy {
        NetworkLidarRepository(
            lidarApiService = lidarRetrofitService,
            okHttpClient = okHttpClient,
            json = json,
            userPreferencesRepository = userPreferencesRepository
        )
    }

    override val fruitPredictor: FruitPredictor by lazy {
        FruitPredictor(context)
    }
}
