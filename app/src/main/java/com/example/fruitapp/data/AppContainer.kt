package com.example.fruitapp.data

/**
 * Dependency Injection container at the application level.
 */
interface AppContainer {
    val esp32MeasurementsRepository: Esp32MeasurementsRepository
    val pressureMeasurementsRepository: PressureMeasurementsRepository
    val esp32CamRepository: Esp32CamRepository
    val measurementsRepository: MeasurementsRepository
    val lidarRepository: LidarRepository
    val fruitPredictor: FruitPredictor
    val userPreferencesRepository: UserPreferencesRepository
}
