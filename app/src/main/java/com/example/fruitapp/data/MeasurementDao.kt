package com.example.fruitapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fruitapp.model.Measurement
import kotlinx.coroutines.flow.Flow

@Dao
interface MeasurementDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(measurement: Measurement)

    @Delete
    suspend fun delete(measurement: Measurement)

    @Query("SELECT * from measurements WHERE id = :id")
    fun getMeasurement(id: Int): Flow<Measurement>

    @Query("SELECT * from measurements ORDER BY id ASC")
    fun getAllMeasurements(): Flow<List<Measurement>>

    @Query("DELETE FROM measurements")
    suspend fun deleteAll()

    @Query("DELETE FROM sqlite_sequence WHERE name='measurements'")
    suspend fun resetId()
}
