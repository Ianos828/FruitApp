package com.example.fruitapp.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.fruitapp.R
import com.example.fruitapp.model.Image
import com.example.fruitapp.model.LidarScan
import com.example.fruitapp.model.Measurement
import com.example.fruitapp.ui.FruitUiState
import com.example.fruitapp.ui.theme.FruitAppTheme
import java.time.LocalDateTime

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
    Card(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))
            
            MediumFruitImage(measurement.image)
            
            Text(
                text = measurement.prediction,
                style = MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.Center
            )
            
            FruitDetailsText(measurement)

            if (measurement.esp32Measurement.lidarScan.scan.isNotEmpty()) {
                // Reserved space for graph to prevent text jumping
                LidarGraphSection(lidarScan = measurement.esp32Measurement.lidarScan)
            }

            ButtonColumn(
                onSaveMeasurementButtonClicked = onSaveMeasurementButtonClicked,
                onDiscardMeasurementButtonClicked = onDiscardMeasurementButtonClicked
            )
        }
    }
}

/**
 * Section that reserves space for the graph
 */
@Composable
private fun LidarGraphSection(
    lidarScan: LidarScan,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp) // Fixed height to keep sensor readings stationary
            .padding(dimensionResource(R.dimen.padding_medium)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "2D Fruit Profile",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LidarGraph(lidarScan = lidarScan)
        }
    }
}

/**
 * Draws the 2D fruit profile graph from lidar scan data
 */
@Composable
private fun LidarGraph(
    lidarScan: LidarScan,
    modifier: Modifier = Modifier
) {
    val lineColor = MaterialTheme.colorScheme.primary

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp)
    ) {
        val points = lidarScan.scan
        val maxStep = points.maxOf { it.step }.toFloat().coerceAtLeast(1f)
        val maxMm = points.maxOf { it.mm }.toFloat()
        val minMm = points.minOf { it.mm }.toFloat()
        val diffMm = (maxMm - minMm).coerceAtLeast(1f)

        val width = size.width
        val height = size.height

        // Draw axes
        drawLine(
            color = Color.Gray,
            start = Offset(0f, height),
            end = Offset(width, height),
            strokeWidth = 2f
        )
        drawLine(
            color = Color.Gray,
            start = Offset(0f, 0f),
            end = Offset(0f, height),
            strokeWidth = 2f
        )

        // Draw the fruit profile line
        val path = Path()
        points.forEachIndexed { index, point ->
            val x = (point.step / maxStep) * width
            // Invert Y: lower mm = closer to sensor = higher on graph
            val y = height - ((point.mm - minMm) / diffMm) * height

            if (index == 0) path.moveTo(x, y)
            else path.lineTo(x, y)
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 3f)
        )
    }
}

/**
 * The fruit image displayed on the screen.
 */
@Composable
private fun MediumFruitImage(
    image: Image,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = ImageRequest.Builder(context = LocalContext.current)
            .data(image.bitmap ?: image.filePath)
            .crossfade(true)
            .build(),
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
 * Displays only the text readings, centered.
 */
@Composable
private fun FruitDetailsText(
    measurement: Measurement,
    modifier: Modifier = Modifier
) {
    Text(
        text = measurement.toString(),
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_medium))
    )
}

/**
 * The buttons at the bottom of the screen after obtaining a measurement
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

@Preview(showBackground = true)
@Composable
fun MeasurementScreenPreview() {
    FruitAppTheme {
        Surface {
            MeasurementScreen(
                fruitUiState = FruitUiState.Success(
                    measurement = Measurement(
                        esp32Measurement = com.example.fruitapp.model.Esp32Measurement(),
                        pressureMeasurement = com.example.fruitapp.model.PressureMeasurement(),
                        image = Image(),
                        prediction = "Banana",
                        date = LocalDateTime.now()
                    )
                ),
                onSaveMeasurementButtonClicked = {},
                onDiscardMeasurementButtonClicked = {},
                retryAction = {}
            )
        }
    }
}
