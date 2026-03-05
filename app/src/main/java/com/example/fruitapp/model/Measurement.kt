package com.example.fruitapp.model

data class Measurement(
    val esp32Measurement: Esp32Measurement,
    val reganMeasurement: ReganMeasurement,
    val id: Int
) {
    /**
     * Returns a string representation of the measurement
     */
    override fun toString(): String {
        return "$esp32Measurement + $reganMeasurement"
    }
}
