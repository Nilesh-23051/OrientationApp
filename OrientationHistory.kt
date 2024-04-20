package com.hello.orientationapp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
fun OrientationHistoryScreen(viewModel: OrientationViewModel = viewModel()) {
    val orientationData by viewModel.orientationData.observeAsState(emptyList())

    Column {
        OrientationHistoryGraph(
            orientationData = orientationData,
            graphColor = Color.Red,
            graphTitle = "Azimuth"
        ) { data -> data.azimuth }

        OrientationHistoryGraph(
            orientationData = orientationData,
            graphColor = Color.Green,
            graphTitle = "Pitch"
        ) { data -> data.pitch }

        OrientationHistoryGraph(
            orientationData = orientationData,
            graphColor = Color.Blue,
            graphTitle = "Roll"
        ) { data -> data.roll }
    }
}

@Composable
fun OrientationHistoryGraph(
    orientationData: List<OrientationData>,
    graphColor: Color,
    graphTitle: String,
    valueExtractor: (OrientationData) -> Float
) {
    Column {
        Text(text = graphTitle, modifier = Modifier.padding(16.dp))
        Canvas(modifier = Modifier.fillMaxSize()) {
            val path = Path()
            orientationData.forEachIndexed { index, data ->
                val x = size.width * (index / orientationData.size.toFloat())
                val y = size.height - (valueExtractor(data) * size.height / 360f)
                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }
            drawPath(
                path = path,
                color = graphColor,
                style = Stroke(width = 4f)
            )
        }
    }
}