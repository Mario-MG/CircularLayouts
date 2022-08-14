package com.mariomg.circularrow.ui.composables

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import com.mariomg.circularrow.model.PI
import com.mariomg.circularrow.model.PolarCoordinates
import com.mariomg.circularrow.model.degToRad
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
        val angleInc = 2 * PI / placeables.size

        layout(width = constraints.maxWidth, height = constraints.maxHeight) {
            placeables.forEachIndexed { index, placeable ->
                val polarCoordinates = PolarCoordinates(
                    radius = radius.value,
                    angle = angularOffset.degToRad() + index * angleInc,
                )
                val coordinates = polarCoordinates.toOffset()
                placeable.placeRelative(
                    x = (centerX + coordinates.x).toInt() - placeable.width / 2,
                    y = (centerY + coordinates.y).toInt() - placeable.height / 2,
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
            val itemRadius = radius * sin(PI / numberOfSiblings)
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
