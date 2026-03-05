package com.example.fruitapp.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.fruitapp.ErrorScreen
import com.example.fruitapp.LoadingScreen
import com.example.fruitapp.R
import com.example.fruitapp.model.Esp32Measurement
import com.example.fruitapp.model.Measurement

/**
 * The screen that displays the measurement information after taking a measurement
 */
@Composable
fun MeasurementScreen(
    fruitUiState: FruitUiState,
    onSaveMeasurementButtonClicked: () -> Unit,
    onDiscardMeasurementButtonClicked: () -> Unit,
    retryAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (fruitUiState) {
        is FruitUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        is FruitUiState.Success -> MeasurementDetailsScreen(
            measurement = fruitUiState.measurement,
            onSaveMeasurementButtonClicked = onSaveMeasurementButtonClicked,
            onDiscardMeasurementButtonClicked = onDiscardMeasurementButtonClicked,
            modifier = modifier.fillMaxWidth()
        )
        is FruitUiState.Error -> ErrorScreen(
            retryAction,
            modifier = modifier.fillMaxSize()
        )
    }
}

@Composable
fun MeasurementDetailsScreen(
    measurement: Measurement,
    onSaveMeasurementButtonClicked: () -> Unit,
    onDiscardMeasurementButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            FruitImage(measurement.reganMeasurement.imageSource)
            FruitDetails(measurement)

            ButtonColumn(
                onSaveMeasurementButtonClicked = onSaveMeasurementButtonClicked,
                onDiscardMeasurementButtonClicked = onDiscardMeasurementButtonClicked
            )
        }
    }
}

/**
 * The fruit image displayed on the screen
 */
@Composable
private fun FruitImage(
    imageSource: String,
    modifier: Modifier = Modifier
){
    AsyncImage(
        model = ImageRequest.Builder(context = LocalContext.current).data(imageSource)
            .crossfade(true).build(),
        error = painterResource(R.drawable.ic_broken_image),
        placeholder = painterResource(R.drawable.loading_img),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(dimensionResource(R.dimen.medium_image_size))
            .padding(dimensionResource(R.dimen.padding_small))
            .clip(MaterialTheme.shapes.small)
    )
}

/**
 * The measurement information displayed on the screen
 */
@Composable
private fun FruitDetails(
    measurement: Measurement,
    modifier: Modifier = Modifier
){
    Text(
        text = measurement.toString(),
        modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))
    )
}

/**
 * The two buttons at the bottom of the screen after obtaining a measurement
 */
@Composable
private fun ButtonColumn(
    onSaveMeasurementButtonClicked: () -> Unit,
    onDiscardMeasurementButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .width(IntrinsicSize.Max)
            .padding(bottom = dimensionResource(R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
    ) {
        SaveMeasurementButton(onSaveMeasurementButtonClicked = onSaveMeasurementButtonClicked)
        DiscardMeasurementButton(onDiscardMeasurementButtonClicked = onDiscardMeasurementButtonClicked)
    }
}

/**
 * Save measurement button
 */
@Composable
private fun SaveMeasurementButton(
    onSaveMeasurementButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onSaveMeasurementButtonClicked,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.save_measurement),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

/**
 * Discard measurement button
 */
@Composable
private fun DiscardMeasurementButton(
    onDiscardMeasurementButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onDiscardMeasurementButtonClicked,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.discard_measurement),
            style = MaterialTheme.typography.labelLarge
        )
    }
}