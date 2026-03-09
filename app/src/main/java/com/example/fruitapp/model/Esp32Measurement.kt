package com.example.fruitapp.model

import android.annotation.SuppressLint
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
    @SuppressLint("DefaultLocale")
    override fun toString(): String {
        return String.format("Fluorescence: %.2f\nNIR 680 Reading: %.2f\n"
                + "NIR 705 Reading: %.2f\nNIR 730 Reading: %.2f\nNIR 760 Reading: %.2f\n"
                + "NIR 810 Reading: %.2f\nNIR 860 Reading: %.2f\nNIR 940 Reading: %.2f\n"
                + "Lidar Reading: %.2f\nEthylene Concentration: %d\nAir Quality: %d\n"
                + "MQ3 Reading: %d\nMQ4 Reading: %d\nMQ5 Reading: %d",
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