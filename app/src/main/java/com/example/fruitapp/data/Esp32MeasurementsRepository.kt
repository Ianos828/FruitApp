package com.example.fruitapp.data

import com.example.fruitapp.model.Esp32Measurement
import com.example.fruitapp.model.LidarPoint
import com.example.fruitapp.model.LidarScan
import com.example.fruitapp.network.Esp32MeasurementApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

interface Esp32MeasurementsRepository {
    suspend fun getMeasurements(): Esp32Measurement
    suspend fun getLidarScan(): LidarScan
    fun getLidarStreaming(): Flow<LidarPoint>
}

class NetworkEsp32MeasurementsRepository(
    private val esp32MeasurementApiService: Esp32MeasurementApiService,
    private val okHttpClient: OkHttpClient = OkHttpClient()
) : Esp32MeasurementsRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getMeasurements(): Esp32Measurement = withContext(Dispatchers.IO) {
        try {
            esp32MeasurementApiService.getMeasurements()
        } catch (e: Exception) {
            e.printStackTrace()
            Esp32Measurement()
        }
    }

    override suspend fun getLidarScan(): LidarScan = withContext(Dispatchers.IO) {
        try {
            esp32MeasurementApiService.getLidarScan()
        } catch (e: Exception) {
            e.printStackTrace()
            LidarScan()
        }
    }

    override fun getLidarStreaming(): Flow<LidarPoint> = callbackFlow {
        val request = Request.Builder()
            .url("ws://esp32_combined.local:81") // ESP32 WebSocket port
            .build()

        val listener = object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val point = json.decodeFromString<LidarPoint>(text)
                    trySend(point)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(1000, null)
                channel.close()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                channel.close(t)
            }
        }

        val webSocket = okHttpClient.newWebSocket(request, listener)
        
        awaitClose {
            webSocket.cancel()
        }
    }
}
