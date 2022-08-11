package com.mariomg.circularrow.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CircularRow(
    modifier: Modifier = Modifier,
    radius: Dp,
    angularOffset: Float = 0f,
    itemsConstraint: CircularRowItemsConstraint = CircularRowItemsConstraint.CONSTRAIN_TO_PARENT_AND_SIBLINGS,
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier,
        content = content,
    ) { measurables, constraints ->
        val newConstraints = when (itemsConstraint) {
            CircularRowItemsConstraint.NONE -> constraints.copy(
                minWidth = 0,
                minHeight = 0,
            )
            CircularRowItemsConstraint.CONSTRAIN_TO_PARENT -> {
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
            CircularRowItemsConstraint.CONSTRAIN_TO_SIBLINGS -> {
                val itemRadius = radius.value * sin(PI / measurables.size)
                val newMaxSize = (2 * itemRadius * sin(PI / 4)).toInt()
                constraints.copy(
                    minWidth = 0,
                    minHeight = 0,
                    maxWidth = newMaxSize,
                    maxHeight = newMaxSize,
                )
            }
            CircularRowItemsConstraint.CONSTRAIN_TO_PARENT_AND_SIBLINGS -> { // TODO: Refactor
                val newMaxWidth = constraints.maxWidth - 2 * radius.value.toInt()
                val newMaxHeight = constraints.maxHeight - 2 * radius.value.toInt()
                check(newMaxWidth > 0) { "Radius cannot be greater than half the parent's width" }
                check(newMaxHeight > 0) { "Radius cannot be greater than half the parent's height" }
                val itemRadius = radius.value * sin(PI / measurables.size)
                val newMaxSize = (2 * itemRadius * sin(PI / 4)).toInt()
                constraints.copy(
                    minWidth = 0,
                    minHeight = 0,
                    maxWidth = Integer.min(newMaxWidth, newMaxSize),
                    maxHeight = Integer.min(newMaxHeight, newMaxSize),
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
                placeable.placeRelative(
                    // TODO: Refactor
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
