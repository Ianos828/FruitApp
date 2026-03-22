package com.example.fruitapp.network

import com.example.fruitapp.model.LidarScan
import retrofit2.http.GET

/**
 * Retrofit service object for Lidar-specific API calls
 */
interface LidarApiService {
    @GET("lidar-scan")
    suspend fun getLidarScan(): LidarScan
}
