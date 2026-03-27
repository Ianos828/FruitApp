package com.example.fruitapp.data

import androidx.room.TypeConverter
import com.example.fruitapp.model.LidarPoint
import com.example.fruitapp.model.LidarScan
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Converters {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun fromTimestamp(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, formatter) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): String? {
        return date?.format(formatter)
    }

    /**
     * Converts a comma-separated string of steps and mm values back to a LidarScan object.
     * Format: "step1,mm1,step2,mm2,..."
     */
    @TypeConverter
    fun fromLidarScanString(value: String?): LidarScan? {
        if (value == null || value.isEmpty()) return LidarScan()
        val parts = value.split(",")
        val points = mutableListOf<LidarPoint>()
        for (i in 0 until parts.size - 1 step 2) {
            val step = parts[i].toIntOrNull() ?: 0
            val mm = parts[i+1].toIntOrNull() ?: 0
            points.add(LidarPoint(step, mm))
        }
        return LidarScan(points)
    }

    /**
     * Converts a LidarScan object to a comma-separated string for database storage.
     * Format: "step1,mm1,step2,mm2,..."
     */
    @TypeConverter
    fun lidarScanToString(lidarScan: LidarScan?): String? {
        if (lidarScan == null) return null
        return lidarScan.scan.joinToString(",") { "${it.step},${it.mm}" }
    }
}
