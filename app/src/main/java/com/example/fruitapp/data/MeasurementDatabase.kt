package com.example.fruitapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.fruitapp.model.Measurement

@Database(entities = [Measurement::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class MeasurementDatabase : RoomDatabase() {
    abstract fun measurementDao(): MeasurementDao

    companion object {
        @Volatile
        private var Instance: MeasurementDatabase? = null

        fun getDatabase(context: Context): MeasurementDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    MeasurementDatabase::class.java,
                    "measurements_database"
                ).fallbackToDestructiveMigration()
                .build()
                .also { Instance = it }
            }
        }
    }
}
