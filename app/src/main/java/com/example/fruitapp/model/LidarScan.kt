package com.example.fruitapp.model

import kotlinx.serialization.Serializable

/**
 * Represents the full 2D lidar scan result from the ESP32
 */
@Serializable
data class LidarScan(
    val scan: List<LidarPoint> = emptyList()
)

/**
 * A single point in the lidar scan
 * @param step the horizontal position of the sensor (motor step index)
 * @param mm the distance in millimetres to the fruit surface at that position
 */
@Serializable
data class LidarPoint(
    val step: Int = 0,
    val mm: Int = 0
)
