package com.example.fruitapp.data

import androidx.annotation.DrawableRes
import com.example.fruitapp.R


/**
 * Data class to represent the measurement from sensors
 */
data class Measurement(
    @DrawableRes val fruitImageResourceId: Int,
    val forceSensorReading: Float,
    val fluorescenceReading: Float,
    val date: String,
    val time: String,
    val nirReadings: List<Float>,
    val lidarReading: Float,
    val ethyleneConcentration: Int,
    val airQuality: Int,
    val mq3Reading: Int,
    val mq4Reading: Int,
    val mq5Reading: Int
) {

    //TODO: fix override method to something nicer
    override fun toString(): String {
        return String.format("Fluorescence: %s\nForce Sensor Reading: %s\nNIR Readings: %s\nLidar Reading: %s\nEthylene Concentration: %s\nAir Quality: %s\nMQ3 Reading: %s\nMQ4 Reading: %s\nMQ5 Reading: %s",
            fluorescenceReading,
            forceSensorReading,
            nirReadings,
            lidarReading,
            ethyleneConcentration,
            airQuality,
            mq3Reading,
            mq4Reading,
            mq5Reading
            )
    }
}

val measurements = listOf(
    Measurement(
        R.drawable.banana,
        1.0f,
        2.0f,
        "10/10/2023",
        "10:10:10",
        listOf(1.0f, 2.0f, 3.0f),
        4.0f,
        5,
        6,
        7,
        8,
        9
    ),
    Measurement(
        R.drawable.banana,
        2.0f,
        3.0f,
        "11/10/2023",
        "10:10:10",
        listOf(1.0f, 2.0f, 3.0f),
        4.0f,
        5,
        6,
        7,
        8,
        9
    ),
    Measurement(
        R.drawable.banana,
        2.0f,
        3.0f,
        "12/10/2023",
        "10:10:10",
        listOf(1.0f, 2.0f, 3.0f),
        4.0f,
        5,
        6,
        7,
        8,
        9
    ),
    Measurement(
        R.drawable.banana,
        2.0f,
        3.0f,
        "13/10/2023",
        "10:10:10",
        listOf(1.0f, 2.0f, 3.0f),
        4.0f,
        5,
        6,
        7,
        8,
        9
    ),
    Measurement(
        R.drawable.banana,
        2.0f,
        3.0f,
        "14/10/2023",
        "10:10:10",
        listOf(1.0f, 2.0f, 3.0f),
        4.0f,
        5,
        6,
        7,
        8,
        9
    ),
    Measurement(
        R.drawable.banana,
        2.0f,
        3.0f,
        "15/10/2023",
        "10:10:10",
        listOf(1.0f, 2.0f, 3.0f),
        4.0f,
        5,
        6,
        7,
        8,
        9
    ),
    Measurement(
        R.drawable.banana,
        2.0f,
        3.0f,
        "16/10/2023",
        "10:10:10",
        listOf(1.0f, 2.0f, 3.0f),
        4.0f,
        5,
        6,
        7,
        8,
        9
    ),
    Measurement(
        R.drawable.banana,
        2.0f,
        3.0f,
        "17/10/2023",
        "10:10:10",
        listOf(1.0f, 2.0f, 3.0f),
        4.0f,
        5,
        6,
        7,
        8,
        9
    ),
    Measurement(
        R.drawable.banana,
        2.0f,
        3.0f,
        "18/10/2023",
        "10:10:10",
        listOf(1.0f, 2.0f, 3.0f),
        4.0f,
        5,
        6,
        7,
        8,
        9
    )
)
