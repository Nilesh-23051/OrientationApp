package com.hello.orientationapp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PredictionScreen(viewModel: OrientationViewModel = viewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val predictions = remember { mutableStateOf<List<Pair<OrientationData, OrientationData>>>(emptyList()) }

    LaunchedEffect(Unit) {
        predictions.value = viewModel.getPredictions()
    }

    Column {
        PredictionGraph(
            predictions = predictions.value,
            graphColor = Color.Red,
            graphTitle = "Azimuth"
        ) { data -> data.azimuth }

        PredictionGraph(
            predictions = predictions.value,
            graphColor = Color.Green,
            graphTitle = "Pitch"
        ) { data -> data.pitch }

        PredictionGraph(
            predictions = predictions.value,
            graphColor = Color.Blue,
            graphTitle = "Roll"
        ) { data -> data.roll }
    }
}

@Composable
fun PredictionGraph(
    predictions: List<Pair<OrientationData, OrientationData>>,
    graphColor: Color,
    graphTitle: String,
    valueExtractor: (OrientationData) -> Float
) {
    Column {
        Text(text = graphTitle, modifier = Modifier.padding(16.dp))
        Canvas(modifier = Modifier.fillMaxWidth()) {
            val path = Path()
            predictions.forEachIndexed { index, pair ->
                val actual = pair.first
                val predicted = pair.second

                val actualX = size.width * (index / predictions.size.toFloat())
                val actualY = size.height - (valueExtractor(actual) * size.height / 360f)
                val predictedX = actualX + (size.width / predictions.size)
                val predictedY = size.height - (valueExtractor(predicted) * size.height / 360f)

                if (index == 0) {
                    path.moveTo(actualX, actualY)
                } else {
                    path.lineTo(actualX, actualY)
                }
                path.moveTo(predictedX, predictedY)
                path.lineTo(predictedX, predictedY)
            }
            drawPath(
                path = path,
                color = graphColor,
                style = Stroke(width = 4f)
            )
        }
    }
}