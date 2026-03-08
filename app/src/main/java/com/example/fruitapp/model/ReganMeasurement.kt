package com.example.fruitapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReganMeasurement(
    @SerialName(value = "matrix") val forceSensorReading: String = "No reading"
) {
    /**
     * Returns a string representation of the measurement
     */
    override fun toString(): String {
        return String.format("Weight: %s", forceSensorReading)
    }
}
