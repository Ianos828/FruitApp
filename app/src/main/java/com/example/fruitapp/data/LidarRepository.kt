package com.example.fruitapp.data

import android.util.Log
import com.example.fruitapp.model.LidarPoint
import com.example.fruitapp.model.LidarScan
import com.example.fruitapp.network.LidarApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
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
    suspend fun stopLidarScan()
}

class NetworkLidarRepository(
    private val lidarApiService: LidarApiService,
    private val okHttpClient: OkHttpClient,
    private val json: Json,
    private val userPreferencesRepository: UserPreferencesRepository
) : LidarRepository {

    private var activeWebSocket: WebSocket? = null

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
        // Force-close any orphaned connection to ensure a fresh start
        activeWebSocket?.cancel()
        activeWebSocket = null
        
        val workingIp = "10.197.233.131"
        val request = Request.Builder()
            .url("ws://$workingIp:81")
            .build()

        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                activeWebSocket = webSocket
                webSocket.send("start_scan")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    if (text.contains("\"done\":true") || text.contains("\"done\": true")) {
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
                if (activeWebSocket == webSocket) activeWebSocket = null
                channel.close()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                if (activeWebSocket == webSocket) activeWebSocket = null
                channel.close(t)
            }
        }

        val webSocket = okHttpClient.newWebSocket(request, listener)
        activeWebSocket = webSocket
        
        awaitClose {
            webSocket.close(1000, "Flow closed")
            if (activeWebSocket == webSocket) activeWebSocket = null
        }
    }

    /**
     * Sends a command to the ESP32 to stop the motor and return to home position.
     */
    override suspend fun stopLidarScan() {
        activeWebSocket?.let { ws ->
            ws.send("stop_scan")
            // Crucial: wait for the packet to send before closing the door
            withContext(Dispatchers.IO) {
                kotlinx.coroutines.delay(200) 
                ws.close(1000, "User stopped scan")
            }
        }
        activeWebSocket = null
    }
}
