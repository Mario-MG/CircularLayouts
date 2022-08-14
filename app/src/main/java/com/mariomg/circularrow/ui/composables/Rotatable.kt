package com.mariomg.circularrow.ui.composables

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import com.mariomg.circularrow.model.radToDeg
import com.mariomg.circularrow.model.toPolarCoordinates

fun Modifier.rotatable(rotatableState: RotatableState) = composed {
    var center by remember { mutableStateOf(Offset(0f, 0f)) }
    onGloballyPositioned { coordinates ->
        val size = coordinates.size
        center = Offset(
            x = size.width / 2f,
            y = size.height / 2f,
        )
    }.then(
        Modifier.pointerInput(Unit) {
            detectDragGestures { change, dragAmount ->
                change.consume()
                val startPositionFromCenter = change.previousPosition - center
                val endPositionFromCenter = startPositionFromCenter + dragAmount
                val startPositionPolar = startPositionFromCenter.toPolarCoordinates()
                val endPositionPolar = endPositionFromCenter.toPolarCoordinates()
                val angleOffsetInc = (endPositionPolar.angle - startPositionPolar.angle).radToDeg()
                rotatableState.rotateBy(angleOffsetInc)
            }
        }
    )
}
