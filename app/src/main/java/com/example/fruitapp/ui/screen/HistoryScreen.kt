package com.example.fruitapp.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.fruitapp.R
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.fruitapp.model.Image
import com.example.fruitapp.model.Measurement
import com.example.fruitapp.ui.HistoryUiState
import com.example.fruitapp.ui.HistoryViewModel
import java.time.format.DateTimeFormatter

/**
 * History screen of the app
 */
@Composable
fun HistoryScreen(
    historyViewModel: HistoryViewModel = viewModel(factory = HistoryViewModel.Factory),
    modifier: Modifier = Modifier
) {
    val historyUiState by historyViewModel.historyUiState.collectAsState()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
        ) {
            items(items = historyUiState.measurementList, key = { it.id }) { measurement ->
                MeasurementItem(
                    measurement = measurement,
                    onDelete = { historyViewModel.deleteMeasurement(measurement) },
                    modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
                )
            }
        }
        
        // Add Clear All button pinned to the bottom of the screen
        if (historyUiState.measurementList.isNotEmpty()) {
            ClearAllButton(
                onClick = { historyViewModel.deleteAllMeasurements() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_medium))
            )
        }
    }
}

/**
 * Clear All button shown at the bottom of the history list
 */
@Composable
private fun ClearAllButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error
        ),
        modifier = modifier
    ) {
        Text(text = "Clear All History")
    }
}

/**
 * Formatted measurements to be displayed in a list
 */
@Composable
private fun MeasurementItem(
    measurement: Measurement,
    onDelete: (Measurement) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val color by animateColorAsState(
        targetValue = if (expanded) MaterialTheme.colorScheme.tertiaryContainer
        else MaterialTheme.colorScheme.primaryContainer
    )

    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
                .background(color = color)
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(id = R.dimen.padding_small)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SmallFruitImage(measurement.image)
                FruitInformation(
                    measurement = measurement,
                    modifier = Modifier.weight(1f).padding(start = dimensionResource(R.dimen.padding_small))
                )
                
                FruitDetailsButton(
                    expanded = expanded,
                    onClick = { expanded = !expanded }
                )
            }

            if (expanded) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(id = R.dimen.padding_small))
                ) {
                    Text(
                        text = measurement.toString(),
                        modifier = Modifier.padding(
                            start = dimensionResource(R.dimen.padding_medium),
                            top = dimensionResource(R.dimen.padding_small),
                            end = dimensionResource(R.dimen.padding_medium),
                            bottom = dimensionResource(R.dimen.padding_medium)
                        )
                    )
                    DeleteButton(
                        onClick = { onDelete(measurement) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DeleteButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Filled.Delete,
            contentDescription = stringResource(R.string.delete),
            tint = MaterialTheme.colorScheme.error
        )
    }
}

/**
 * Small image for list items. Loads from filePath since these are saved records.
 */
@Composable
private fun SmallFruitImage(
    image: Image,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = ImageRequest.Builder(context = LocalContext.current)
            .data(image.filePath)
            .crossfade(true)
            .build(),
        error = painterResource(R.drawable.ic_broken_image),
        placeholder = painterResource(R.drawable.loading_img),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(dimensionResource(R.dimen.small_image_size))
            .clip(MaterialTheme.shapes.small)
    )
}

@Composable
private fun FruitInformation(
    measurement: Measurement,
    modifier: Modifier = Modifier
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    Column(modifier = modifier) {
        Text(
            text = measurement.prediction,
            style = MaterialTheme.typography.displayMedium
        )
        Text(
            text = measurement.date.format(formatter),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun FruitDetailsButton(
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ){
        Icon(
            imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
            contentDescription = stringResource(R.string.expand_button_content_description),
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}
