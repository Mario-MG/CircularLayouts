package com.mariomg.circularrow.ui.composables

import android.util.Log
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import kotlin.math.*

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
        val newConstraints = when (itemsConstraint) { // TODO: Refactor
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
@Composable
fun CircularRow(
    modifier: Modifier = Modifier,
    radius: Dp,
    rotatableState: RotatableState,
    itemsConstraint: CircularRowItemsConstraint = CircularRowItemsConstraint.CONSTRAIN_TO_PARENT_AND_SIBLINGS,
    content: @Composable () -> Unit,
) {
    val offset  = rotatableState.angularOffset

    CircularRow(
        modifier = modifier,
        radius = radius,
        angularOffset = offset,
        itemsConstraint = itemsConstraint,
        content = content,
    )
}

class RotatableState(initialAngularOffset: Float = 0f) {
    private var _angularOffset by mutableStateOf(initialAngularOffset)
    var angularOffset: Float
        get() = _angularOffset
        private set(value) {
            if (value != _angularOffset) {
                _angularOffset = value
            }
        }

    fun rotateBy(angularOffset: Float) {
        this.angularOffset += angularOffset
    }

    fun rotateTo(angularOffset: Float) {
        this.angularOffset = angularOffset
    }

    companion object {
        val Saver: Saver<RotatableState, *> = Saver(
            save = { it._angularOffset },
            restore = { RotatableState(it) }
        )
    }
}

@Composable
fun rememberRotatableState(initialAngularOffset: Float = 0f): RotatableState {
    return rememberSaveable(saver = RotatableState.Saver) {
        RotatableState(initialAngularOffset = initialAngularOffset)
    }
}

enum class CircularRowItemsConstraint {
    NONE,
    CONSTRAIN_TO_PARENT,
    CONSTRAIN_TO_SIBLINGS,
    CONSTRAIN_TO_PARENT_AND_SIBLINGS,
}

fun Modifier.rotatable(rotatableState: RotatableState) = composed {
    var center by remember { mutableStateOf(Offset(0f, 0f)) }
    var startPositionPolar by remember { mutableStateOf(PolarCoordinates(0f, 0f)) }
    var endPositionPolar by remember { mutableStateOf(PolarCoordinates(0f, 0f)) }
    onGloballyPositioned { coordinates ->
        val size = coordinates.size
        Log.d("TEST", "center: $center")
        center = Offset(
            x = size.width / 2f,
            y = size.height / 2f,
        )
    }.then(
        Modifier.pointerInput(Unit) {
            detectDragGestures { change, dragAmount ->
                change.consume()
                val startPositionFromCenter = change.previousPosition - center
                Log.d("TEST", "startPos: $startPositionFromCenter")
                val endPositionFromCenter = startPositionFromCenter + dragAmount
                Log.d("TEST", "endPos: $endPositionFromCenter")
                startPositionPolar = startPositionFromCenter.toPolarCoordinates()
                Log.d("TEST", "startPosPolar: $startPositionPolar")
                endPositionPolar = endPositionFromCenter.toPolarCoordinates()
                Log.d("TEST", "endPosPolar: $endPositionPolar")
                val angleOffsetInc = (endPositionPolar.angle - startPositionPolar.angle).radToDeg()
                Log.d("TEST", "offsetInc: $angleOffsetInc")
                rotatableState.rotateBy(angleOffsetInc)
            }
        }
    )
}

data class PolarCoordinates(
    val radius: Float,
    val angle: Float,
) {
    fun toOffset() = Offset(
        x = radius * sin(angle),
        y = -radius * cos(angle),
    )
}

fun Offset.toPolarCoordinates() = PolarCoordinates(
    radius = sqrt(x * x + y * y),
    angle = when {
        x >= 0 && y < 0 -> atan(-x / y)
        x >= 0 && y >= 0 -> (atan(y / x) + PI / 2).toFloat()
        x < 0 && y >= 0 -> (atan(-x / y) + PI).toFloat()
        else -> (atan(y / x) + 3 * PI / 2).toFloat()
    },
)

fun Float.degToRad() = (this * PI / 180).toFloat()

fun Float.radToDeg() = (this * 180 / PI).toFloat()
