package com.hello.orientationapp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun OrientationScreen(
    viewModel: OrientationViewModel = viewModel(),
    onHistoryClick: () -> Unit,
    onPredictionClick: () -> Unit
) {
    val orientationData by viewModel.orientationData.observeAsState(emptyList())
    val azimuth by viewModel.azimuth.observeAsState(0f)
    val pitch by viewModel.pitch.observeAsState(0f)
    val roll by viewModel.roll.observeAsState(0f)

    Column {
        Text("Azimuth: $azimuth", modifier = Modifier.padding(16.dp))
        Text("Pitch: $pitch", modifier = Modifier.padding(16.dp))
        Text("Roll: $roll", modifier = Modifier.padding(16.dp))

        Button(onClick = onHistoryClick, modifier = Modifier.padding(16.dp)) {
            Text(text = "View History")
        }

        Button(onClick = onPredictionClick, modifier = Modifier.padding(16.dp)) {
            Text(text = "View Predictions")
        }
    }
}

@Composable
fun OrientationHistoryGraph(orientationData: List<OrientationData>) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val path = Path()
        orientationData.forEachIndexed { index, data ->
            val x = size.width * (index / orientationData.size.toFloat())
            val y = size.height - (data.azimuth * size.height / 360f)
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        drawPath(
            path = path,
            color = Color.Blue,
            style = Stroke(width = 4f)
        )
    }
}