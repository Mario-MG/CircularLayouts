package com.mariomg.circularlayouts.circularrow

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import com.mariomg.circularlayouts.CircularLayoutDirection
import com.mariomg.circularlayouts.CircularLayoutItemRotation
import com.mariomg.circularlayouts.CircularLayoutItemsConstraints
import com.mariomg.circularlayouts.Rotation
import com.mariomg.circularlayouts.model.PolarCoordinates
import com.mariomg.circularlayouts.rotation.RotationState
import com.mariomg.circularlayouts.unit.Degrees
import com.mariomg.circularlayouts.unit.degrees
import com.mariomg.circularlayouts.unit.times

@Composable
fun CircularRow(
    modifier: Modifier = Modifier,
    radius: Dp,
    angularOffset: Degrees = 0.degrees,
    itemsConstraint: CircularLayoutItemsConstraints = CircularLayoutItemsConstraints.CONSTRAIN_TO_PARENT_AND_SIBLINGS,
    direction: CircularLayoutDirection = CircularLayoutDirection.CLOCKWISE,
    itemRotation: Rotation = CircularLayoutItemRotation.NONE,
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier,
        content = content,
    ) { measurables, constraints ->
        val newConstraints = itemsConstraint.calculateConstraints(
            parentConstraints = constraints,
            radius = radius.value,
            numberOfSiblings = measurables.size,
        )
        val centerX = constraints.maxWidth / 2
        val centerY = constraints.maxHeight / 2

        val placeables = measurables.map { measurable ->
            measurable.measure(newConstraints)
        }
        val angleIncAbsolute = 360.degrees / placeables.size
        val angleInc = when (direction) {
            CircularLayoutDirection.CLOCKWISE -> angleIncAbsolute
            CircularLayoutDirection.COUNTERCLOCKWISE -> -angleIncAbsolute
        }

        layout(width = constraints.maxWidth, height = constraints.maxHeight) {
            placeables.forEachIndexed { index, placeable ->
                val angle = angularOffset + index * angleInc
                val polarCoordinates = PolarCoordinates.usingDegrees(
                    radius = radius.value,
                    angle = angle,
                )
                val coordinates = polarCoordinates.toOffset()
                placeable.placeRelativeWithLayer(
                    x = (centerX + coordinates.x).toInt() - placeable.width / 2,
                    y = (centerY + coordinates.y).toInt() - placeable.height / 2,
                ) {
                    rotationZ = itemRotation(angle).value
                }
            }
        }
    }
}
@Composable
fun CircularRow(
    modifier: Modifier = Modifier,
    radius: Dp,
    rotationState: RotationState,
    itemsConstraint: CircularLayoutItemsConstraints = CircularLayoutItemsConstraints.CONSTRAIN_TO_PARENT_AND_SIBLINGS,
    direction: CircularLayoutDirection = CircularLayoutDirection.CLOCKWISE,
    itemRotation: Rotation = CircularLayoutItemRotation.NONE,
    content: @Composable () -> Unit,
) {
    val offset  = rotationState.angularOffset

    CircularRow(
        modifier = modifier,
        radius = radius,
        angularOffset = offset,
        itemsConstraint = itemsConstraint,
        direction = direction,
        itemRotation = itemRotation,
        content = content,
    )
}
