package com.mariomg.circularrow.ui

import androidx.compose.ui.unit.Constraints
import com.mariomg.circularrow.model.PI
import kotlin.math.sin

enum class CircularRowItemsConstraints {
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
