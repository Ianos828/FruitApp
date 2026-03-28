package com.example.fruitapp.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.fruitapp.R
import com.example.fruitapp.ui.theme.FruitAppTheme

/**
 * Home screen of the app
 */
@Composable
fun FruitAppHomeScreen(
    modifier: Modifier = Modifier,
    onMeasureButtonClicked: () -> Unit,
    onHistoryButtonClicked: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.banana),
            contentDescription = null,
            modifier = Modifier.width(dimensionResource(R.dimen.large_image_size))
        )
        Column(
            modifier = Modifier
                .width(IntrinsicSize.Max)
                .padding(bottom = dimensionResource(R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
        ) {
            Button(
                onClick = onMeasureButtonClicked,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.take_measurement),
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Button(
                onClick = onHistoryButtonClicked,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.history),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    FruitAppTheme {
        Surface {
            FruitAppHomeScreen(
                onMeasureButtonClicked = {},
                onHistoryButtonClicked = {}
            )
        }
    }
}
