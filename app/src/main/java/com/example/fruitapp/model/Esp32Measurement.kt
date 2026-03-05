package com.example.fruitapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data class to represent the measurement from sensors
 */
@Serializable
data class Esp32Measurement(
    @SerialName(value = "fluorescence") val fluorescenceReading: Float = 0.0f,
    @SerialName(value = "nir_680") val nir680Reading: Float = 0.0f,
    @SerialName(value = "nir_705") val nir705Reading: Float = 0.0f,
    @SerialName(value = "nir_730") val nir730Reading: Float = 0.0f,
    @SerialName(value = "nir_760") val nir760Reading: Float = 0.0f,
    @SerialName(value = "nir_810") val nir810Reading: Float = 0.0f,
    @SerialName(value = "nir_860") val nir860Reading: Float = 0.0f,
    @SerialName(value = "nir_940") val nir940Reading: Float = 0.0f,
    @SerialName(value = "distance_mm") val lidarReading: Float = 0.0f,
    @SerialName(value = "ethylene_ppb") val ethyleneConcentration: Int = 0,
    @SerialName(value = "air_quality") val airQuality: Int = 0,
    @SerialName(value = "mq3") val mq3Reading: Int = 0,
    @SerialName(value = "mq4") val mq4Reading: Int = 0,
    @SerialName(value = "mq5") val mq5Reading: Int = 0
) {

    //TODO: fix override method to something nicer
    /**
     * Returns a string representation of the measurement
     */
    override fun toString(): String {
        return String.format("Fluorescence: %s\nNIR 680 Reading: %s\n"
                + "NIR 705 Reading: %s\nNIR 730 Reading: %s\nNIR 760 Reading: %s\n"
                + "NIR 810 Reading: %s\nNIR 860 Reading: %s\nNIR 940 Reading: %s\n"
                + "Lidar Reading: %s\nEthylene Concentration: %s\nAir Quality: %s\n"
                + "MQ3 Reading: %s\nMQ4 Reading: %s\nMQ5 Reading: %s",
            fluorescenceReading,
            nir680Reading,
            nir705Reading,
            nir730Reading,
            nir760Reading,
            nir810Reading,
            nir860Reading,
            nir940Reading,
            lidarReading,
            ethyleneConcentration,
            airQuality,
            mq3Reading,
            mq4Reading,
            mq5Reading
        )
    }
}