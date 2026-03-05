package com.example.fruitapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReganMeasurement(
    @SerialName(value = "image_url") val imageSource: String = "", //imageUrl
    @SerialName(value = "force_sensor_reading") val forceSensorReading: Float = 0.0f
) {
    /**
     * Returns a string representation of the measurement
     */
    override fun toString(): String {
        return String.format("Weight: %s", forceSensorReading)
    }
}
