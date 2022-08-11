package com.mariomg.circularrow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mariomg.circularrow.CircularRowItemsConstraint.*
import com.mariomg.circularrow.ui.theme.CircularRowTheme
import java.lang.Integer.min
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

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
                    var numberOfClicks by remember { mutableStateOf(0) }
                    val offset by animateFloatAsState(
                        targetValue = numberOfClicks * 360f,
                        animationSpec = tween(3000),
                    )
                    CircularRow(
                        modifier = Modifier.weight(1f),
                        radius = 400.dp,
                        angularOffset = offset,
                    ) {
                        for (index in 1..5) {
                            Text("Prueba de CircularRow $index", modifier = Modifier.background(Color.Red))
                        }
                    }

                    Button(onClick = { numberOfClicks++ }) {
                        Text("Rotate!")
                    }
                }
            }
        }
    }
}

@Composable
fun CircularRow(
    modifier: Modifier = Modifier,
    radius: Dp,
    angularOffset: Float = 0f,
    itemsConstraint: CircularRowItemsConstraint = CONSTRAIN_TO_PARENT_AND_SIBLINGS,
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier,
        content = content,
    ) { measurables, constraints ->
        val newConstraints = when (itemsConstraint) {
            NONE -> constraints.copy(
                minWidth = 0,
                minHeight = 0,
            )
            CONSTRAIN_TO_PARENT -> {
                val newMaxWidth = constraints.maxWidth - 2 * radius.value.toInt()
                val newMaxHeight = constraints.maxHeight - 2 * radius.value.toInt()
                check(newMaxWidth > 0) { "Radius cannot be greater than half the parent's width" }
                check(newMaxHeight > 0) { "Radius cannot be greater than half the parent's height" }
                constraints.copy(
                    minWidth = 0,
                    minHeight = 0,
                    maxWidth = newMaxWidth,
                    maxHeight = newMaxHeight,
                )
            }
            CONSTRAIN_TO_SIBLINGS -> {
                val itemRadius = radius.value * sin(PI / measurables.size)
                val newMaxSize = (2 * itemRadius * sin(PI / 4)).toInt()
                constraints.copy(
                    minWidth = 0,
                    minHeight = 0,
                    maxWidth = newMaxSize,
                    maxHeight = newMaxSize,
                )
            }
            CONSTRAIN_TO_PARENT_AND_SIBLINGS -> { // TODO: Refactor
                val newMaxWidth = constraints.maxWidth - 2 * radius.value.toInt()
                val newMaxHeight = constraints.maxHeight - 2 * radius.value.toInt()
                check(newMaxWidth > 0) { "Radius cannot be greater than half the parent's width" }
                check(newMaxHeight > 0) { "Radius cannot be greater than half the parent's height" }
                val itemRadius = radius.value * sin(PI / measurables.size)
                val newMaxSize = (2 * itemRadius * sin(PI / 4)).toInt()
                constraints.copy(
                    minWidth = 0,
                    minHeight = 0,
                    maxWidth = min(newMaxWidth, newMaxSize),
                    maxHeight = min(newMaxHeight, newMaxSize),
                )
            }
        }
        val placeables = measurables.map { measurable ->
            measurable.measure(newConstraints)
        }
        val centerX = constraints.maxWidth / 2
        val centerY = constraints.maxHeight / 2

        layout(width = constraints.maxWidth, height = constraints.maxHeight) {
            placeables.forEachIndexed { index, placeable ->
                placeable.placeRelative( // TODO: Refactor
                    x = (centerX + radius.value * sin(angularOffset.degToRad() + index * (2 * PI / placeables.size))).toInt() - placeable.width / 2,
                    y = (centerY - radius.value * cos(angularOffset.degToRad() + index * (2 * PI / placeables.size))).toInt() - placeable.height / 2,
                )
            }
        }
    }
}

enum class CircularRowItemsConstraint {
    NONE,
    CONSTRAIN_TO_PARENT,
    CONSTRAIN_TO_SIBLINGS,
    CONSTRAIN_TO_PARENT_AND_SIBLINGS,
}

private fun Float.degToRad() = this * PI / 180
