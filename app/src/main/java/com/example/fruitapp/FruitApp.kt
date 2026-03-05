package com.example.fruitapp

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fruitapp.ui.FruitAppHomeScreen
import com.example.fruitapp.ui.FruitViewModel
import com.example.fruitapp.ui.HistoryScreen
import com.example.fruitapp.ui.MeasurementScreen

/**
 * Enum to represent the screens in the app
 */
enum class FruitAppScreen(@StringRes val title: Int) {
    Start(title = R.string.app_name),
    Measurement(title = R.string.measurement),
    History(title = R.string.history)
}

@Composable
fun FruitApp(
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = FruitAppScreen.valueOf(
        backStackEntry?.destination?.route ?: FruitAppScreen.Start.name
    )

    // Create ViewModel using the Factory
    val viewModel: FruitViewModel = viewModel(factory = FruitViewModel.Factory)

    Scaffold(
        topBar = {
            val layoutDirection = LocalLayoutDirection.current
            FruitAppAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = WindowInsets.safeDrawing.asPaddingValues()
                            .calculateStartPadding(layoutDirection),
                        end = WindowInsets.safeDrawing.asPaddingValues()
                            .calculateEndPadding(layoutDirection),
                    )
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = FruitAppScreen.Start.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = FruitAppScreen.Start.name) {
                FruitAppHomeScreen (
                    onMeasureButtonClicked = {
                        viewModel.getMeasurement()
                        navController.navigate(FruitAppScreen.Measurement.name)
                    },
                    onHistoryButtonClicked = { navController.navigate(FruitAppScreen.History.name) },
                    modifier = Modifier.padding(innerPadding)
                        .fillMaxSize()
                )
            }
            composable(route = FruitAppScreen.Measurement.name) {
                MeasurementScreen(
                    fruitUiState = viewModel.fruitUiState,
                    onSaveMeasurementButtonClicked = {
                        viewModel.saveCurrentMeasurement()
                        navController.navigate(FruitAppScreen.History.name) {
                            popUpTo(FruitAppScreen.Start.name) {
                                inclusive = false
                            }
                        }
                    },
                    onDiscardMeasurementButtonClicked = {
                        // Just navigate back to Start without saving
                        navController.navigate(FruitAppScreen.Start.name) {
                            popUpTo(FruitAppScreen.Start.name) {
                                inclusive = true
                            }
                        }
                    },
                    retryAction = viewModel::getMeasurement,
                    modifier = Modifier.padding(innerPadding)
                        .fillMaxSize()
                )
            }
            composable(route = FruitAppScreen.History.name) {
                HistoryScreen(
                    fruitUiState = viewModel.fruitUiState,
                    innerPadding = innerPadding,
                    retryAction = viewModel::getMeasurement,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FruitAppAppBar(
    currentScreen: FruitAppScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    modifier = Modifier
                        .size(dimensionResource(id = R.dimen.small_image_size))
                        .padding(dimensionResource(id = R.dimen.padding_small)),
                    painter = painterResource(R.drawable.banana),
                    contentDescription = null
                )
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.displayLarge
                )
            }
        },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        },

        //TODO: Add delete button
        modifier = modifier
    )
}
