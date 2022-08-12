package com.mariomg.circularrow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import com.mariomg.circularrow.ui.composables.CircularRow
import com.mariomg.circularrow.ui.composables.PolarCoordinates
import com.mariomg.circularrow.ui.composables.radToDeg
import com.mariomg.circularrow.ui.composables.toPolarCoordinates
import com.mariomg.circularrow.ui.theme.CircularRowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CircularRowTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.background),
                ) {
                    var center by remember { mutableStateOf(Offset(0f, 0f)) }
                    var startPositionPolar by remember { mutableStateOf(PolarCoordinates(0f, 0f)) }
                    var currentPositionPolar by remember { mutableStateOf(PolarCoordinates(0f, 0f)) }
                    var offset by remember { mutableStateOf(0f) }
                    CircularRow(
                        modifier = Modifier
                            .weight(1f)
                            .onGloballyPositioned { coordinates ->
                                val size = coordinates.size
                                center = Offset(
                                    x = size.width / 2f,
                                    y = size.height / 2f,
                                )
                            }
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    val startPositionFromCenter = change.previousPosition - center
                                    val endPositionFromCenter =
                                        startPositionFromCenter + dragAmount
                                    startPositionPolar =
                                        startPositionFromCenter.toPolarCoordinates()
                                    currentPositionPolar =
                                        endPositionFromCenter.toPolarCoordinates()
                                    offset += (currentPositionPolar.angle - startPositionPolar.angle).radToDeg()
                                }
                            },
                        radius = 400.dp,
                        angularOffset = offset,
                    ) {
                        for (index in 1..5) {
                            Text("Prueba de CircularRow $index", modifier = Modifier.background(Color.Red))
                        }
                    }
                }
            }
        }
    }
}
