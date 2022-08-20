package com.mariomg.circularrow.ui

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import com.mariomg.circularrow.model.PolarCoordinates
import com.mariomg.circularrow.model.unit.degrees

@Composable
fun CircularRow(
    modifier: Modifier = Modifier,
    radius: Dp,
    angularOffset: Float = 0f,
    itemsConstraint: CircularRowItemsConstraints = CircularRowItemsConstraints.CONSTRAIN_TO_PARENT_AND_SIBLINGS,
    direction: CircularRowDirection = CircularRowDirection.CLOCKWISE,
    itemRotation: Rotation = CircularRowItemRotation.NONE,
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
        val angleIncAbsolute = 360f / placeables.size
        val angleInc = when (direction) {
            CircularRowDirection.CLOCKWISE -> angleIncAbsolute
            CircularRowDirection.COUNTERCLOCKWISE -> -angleIncAbsolute
        }

        layout(width = constraints.maxWidth, height = constraints.maxHeight) {
            placeables.forEachIndexed { index, placeable ->
                val angle = (angularOffset + index * angleInc).degrees
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
    itemsConstraint: CircularRowItemsConstraints = CircularRowItemsConstraints.CONSTRAIN_TO_PARENT_AND_SIBLINGS,
    direction: CircularRowDirection = CircularRowDirection.CLOCKWISE,
    itemRotation: Rotation = CircularRowItemRotation.NONE,
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