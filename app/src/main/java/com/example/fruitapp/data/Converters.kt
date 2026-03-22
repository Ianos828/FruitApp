package com.example.fruitapp.data

import androidx.room.TypeConverter
import com.example.fruitapp.model.LidarScan
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Converters {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromTimestamp(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, formatter) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): String? {
        return date?.format(formatter)
    }

    @TypeConverter
    fun fromLidarScan(value: String?): LidarScan? {
        return value?.let { json.decodeFromString<LidarScan>(it) }
    }

    @TypeConverter
    fun lidarScanToString(lidarScan: LidarScan?): String? {
        return lidarScan?.let { json.encodeToString(it) }
    }
}
