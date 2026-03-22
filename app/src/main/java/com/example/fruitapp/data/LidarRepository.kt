package com.example.fruitapp.data

import com.example.fruitapp.model.LidarPoint
import com.example.fruitapp.model.LidarScan
import com.example.fruitapp.network.LidarApiService
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

interface LidarRepository {
    suspend fun getLidarScan(): LidarScan
    fun getLidarStreaming(): Flow<LidarPoint>
}

class NetworkLidarRepository(
    private val lidarApiService: LidarApiService,
    private val okHttpClient: OkHttpClient = OkHttpClient()
) : LidarRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getLidarScan(): LidarScan = withContext(Dispatchers.IO) {
        try {
            lidarApiService.getLidarScan()
        } catch (e: Exception) {
            e.printStackTrace()
            LidarScan()
        }
    }

    /**
     * Streams Lidar points from the ESP32 via a WebSocket connection.
     */
    override fun getLidarStreaming(): Flow<LidarPoint> = callbackFlow {
        val request = Request.Builder()
            .url("ws://esp32_combined.local:81") // ESP32 WebSocket port
            .build()

        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                // Arduino is Scenario B, needs the one line to send start_scan after WebSocket connects.
                webSocket.send("start_scan")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    // Check if it is the done signal
                    if (text.contains("\"done\": true")) {
                        channel.close()
                        return
                    }

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
