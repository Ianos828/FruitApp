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
    val lidarScan: LidarScan = LidarScan(),
    @SerialName(value = "ethylene_ppb") val ethyleneConcentration: Int = 0,
    @SerialName(value = "air_quality") val airQuality: Int = 0,
    @SerialName(value = "mq3") val mq3Reading: Int = 0,
    @SerialName(value = "mq4") val mq4Reading: Int = 0,
    @SerialName(value = "mq5") val mq5Reading: Int = 0
) {

    /**
     * Checks if the measurement is the default value
     */
    fun isDefault(): Boolean {
        return this.fluorescenceReading == 0.0f &&
                this.nir680Reading == 0.0f &&
                this.nir705Reading == 0.0f &&
                this.nir730Reading == 0.0f &&
                this.nir760Reading == 0.0f &&
                this.nir810Reading == 0.0f &&
                this.nir860Reading == 0.0f &&
                this.nir940Reading == 0.0f &&
                this.lidarScan.scan.isEmpty() &&
                this.ethyleneConcentration == 0 &&
                this.airQuality == 0 &&
                this.mq3Reading == 0 &&
                this.mq4Reading == 0 &&
                this.mq5Reading == 0
    }

    /**
     * Returns a string representation of the measurement
     */
    @SuppressLint("DefaultLocale")
    override fun toString(): String {
        if (isDefault()) {
            return "No sensor array data received."
        }

        return String.format("Fluorescence: %.2f\nNIR 680 Reading: %.2f\n"
                + "NIR 705 Reading: %.2f\nNIR 730 Reading: %.2f\nNIR 760 Reading: %.2f\n"
                + "NIR 810 Reading: %.2f\nNIR 860 Reading: %.2f\nNIR 940 Reading: %.2f\n"
                + "Lidar Scan Points: %d\nEthylene Concentration: %d\nAir Quality: %d\n"
                + "MQ3 Reading: %d\nMQ4 Reading: %d\nMQ5 Reading: %d",
            fluorescenceReading,
            nir680Reading,
            nir705Reading,
            nir730Reading,
            nir760Reading,
            nir810Reading,
            nir860Reading,
            nir940Reading,
            lidarScan.scan.size,
            ethyleneConcentration,
            airQuality,
            mq3Reading,
            mq4Reading,
            mq5Reading
        )
    }
}
