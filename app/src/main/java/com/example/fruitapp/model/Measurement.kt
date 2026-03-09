package com.example.fruitapp.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "measurements")
data class Measurement(
    @Embedded val esp32Measurement: Esp32Measurement,
    @Embedded val reganMeasurement: ReganMeasurement,
    @Embedded val image: Image,
    val date: LocalDateTime = LocalDateTime.now(),
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
) {
    /**
     * Returns a string representation of the measurement
     */
    override fun toString(): String {
        return "$esp32Measurement\n$reganMeasurement"
    }
}
