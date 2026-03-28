package com.example.fruitapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * Repository that handles saving and retrieving user preferences.
 */
class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    private companion object {
        val ESP32_IP = stringPreferencesKey("esp32_ip")
        val PRESSURE_SENSOR_IP = stringPreferencesKey("pressure_sensor_ip")
        val ESP32_CAM_IP = stringPreferencesKey("esp32_cam_ip")
    }

    val esp32Ip: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[ESP32_IP] ?: "esp32_combined.local"
        }

    val pressureSensorIp: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PRESSURE_SENSOR_IP] ?: "force_sensor.local"
        }

    val esp32CamIp: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[ESP32_CAM_IP] ?: "esp32_cam_image.local"
        }

    suspend fun saveEsp32Ip(ip: String) {
        dataStore.edit { preferences ->
            preferences[ESP32_IP] = ip
        }
    }

    suspend fun savePressureSensorIp(ip: String) {
        dataStore.edit { preferences ->
            preferences[PRESSURE_SENSOR_IP] = ip
        }
    }

    suspend fun saveEsp32CamIp(ip: String) {
        dataStore.edit { preferences ->
            preferences[ESP32_CAM_IP] = ip
        }
    }
}
