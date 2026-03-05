package com.example.fruitapp.ui

import com.example.fruitapp.model.Measurement

/**
 * UI state for the Fruit App
 */
sealed interface FruitUiState {
    data class Success(
        val measurement: Measurement,
        val measurements: List<Measurement> = listOf()
    ) : FruitUiState
    object Error : FruitUiState
    object Loading : FruitUiState
}
