package com.mariomg.circularrow.ui.composables

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import com.mariomg.circularrow.model.PI
import com.mariomg.circularrow.model.PolarCoordinates
import kotlin.math.*

@Composable
fun CircularRow(
    modifier: Modifier = Modifier,
    radius: Dp,
    angularOffset: Float = 0f,
    itemsConstraint: CircularRowItemsConstraint = CircularRowItemsConstraint.CONSTRAIN_TO_PARENT_AND_SIBLINGS,
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
                val angle = angularOffset + index * angleInc
                val polarCoordinates = PolarCoordinates.usingDegrees(
                    radius = radius.value,
                    angleInDeg = angle,
                )
                val coordinates = polarCoordinates.toOffset()
                placeable.placeRelativeWithLayer(
                    x = (centerX + coordinates.x).toInt() - placeable.width / 2,
                    y = (centerY + coordinates.y).toInt() - placeable.height / 2,
                ) {
                    rotationZ = itemRotation(angle)
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
    itemsConstraint: CircularRowItemsConstraint = CircularRowItemsConstraint.CONSTRAIN_TO_PARENT_AND_SIBLINGS,
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

enum class CircularRowDirection {
    CLOCKWISE,
    COUNTERCLOCKWISE,
}

enum class CircularRowItemsConstraint {
    NONE {
        override fun calculateConstraints(
            parentConstraints: Constraints,
            radius: Float,
            numberOfSiblings: Int,
        ) = parentConstraints.copy(
            minWidth = 0,
            minHeight = 0,
        )
    },
    CONSTRAIN_TO_PARENT {
        override fun calculateConstraints(
            parentConstraints: Constraints,
            radius: Float,
            numberOfSiblings: Int,
        ): Constraints {
            val newMaxWidth = parentConstraints.maxWidth - 2 * radius.toInt()
            val newMaxHeight = parentConstraints.maxHeight - 2 * radius.toInt()
            check(newMaxWidth > 0) { "Radius cannot be greater than half the parent's width" }
            check(newMaxHeight > 0) { "Radius cannot be greater than half the parent's height" }
            return parentConstraints.copy(
                minWidth = 0,
                minHeight = 0,
                maxWidth = newMaxWidth,
                maxHeight = newMaxHeight,
            )
        }
    },
    CONSTRAIN_TO_SIBLINGS {
        override fun calculateConstraints(
            parentConstraints: Constraints,
            radius: Float,
            numberOfSiblings: Int,
        ): Constraints {
            val itemRadius = if (numberOfSiblings > 1) radius * sin(PI / numberOfSiblings) else radius
            val newMaxSize = (2 * itemRadius * sin(PI / 4)).toInt()
            return parentConstraints.copy(
                minWidth = 0,
                minHeight = 0,
                maxWidth = newMaxSize,
                maxHeight = newMaxSize,
            )
        }
    },
    CONSTRAIN_TO_PARENT_AND_SIBLINGS {
        override fun calculateConstraints(
            parentConstraints: Constraints,
            radius: Float,
            numberOfSiblings: Int,
        ): Constraints {
            val constraintsToParent = CONSTRAIN_TO_PARENT.calculateConstraints(
                parentConstraints = parentConstraints,
                radius = radius,
                numberOfSiblings = numberOfSiblings,
            )
            val constraintsToSiblings = CONSTRAIN_TO_SIBLINGS.calculateConstraints(
                parentConstraints = parentConstraints,
                radius = radius,
                numberOfSiblings = numberOfSiblings,
            )
            return parentConstraints.copy(
                minWidth = 0,
                minHeight = 0,
                maxWidth = Integer.min(constraintsToParent.maxWidth, constraintsToSiblings.maxWidth),
                maxHeight = Integer.min(constraintsToParent.maxHeight, constraintsToSiblings.maxHeight),
            )
        }
    },
    ;

    abstract fun calculateConstraints(
        parentConstraints: Constraints,
        radius: Float,
        numberOfSiblings: Int,
    ): Constraints
}

typealias Rotation = (Float) -> Float

enum class CircularRowItemRotation(private val rotation: Rotation) : Rotation {
    NONE(rotation = { 0f }),
    TANGENT(rotation = { it }),
    PERPENDICULAR_CLOCKWISE(rotation = { it + 90 }),
    PERPENDICULAR_COUNTERCLOCKWISE(rotation = { it - 90 }),
    TANGENT_INVERSE(rotation = { it + 180 }),
    ;

    override fun invoke(angle: Float) = rotation(angle)
}
