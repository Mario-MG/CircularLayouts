package com.mariomg.circularlayouts.rotation

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import com.mariomg.circularlayouts.model.AngularVelocity
import com.mariomg.circularlayouts.model.toPolarCoordinates
import com.mariomg.circularlayouts.unit.degrees
import kotlinx.coroutines.launch

fun Modifier.rotatable(rotationState: RotationState) = composed {
    val coroutineScope = rememberCoroutineScope()
    var center by remember { mutableStateOf(Offset(0f, 0f)) }
    var lastVelocity by remember { mutableStateOf(AngularVelocity(0.degrees)) }
    onGloballyPositioned { coordinates ->
        val size = coordinates.size
        center = Offset(
            x = size.width / 2f,
            y = size.height / 2f,
        )
    }.then(
        Modifier.pointerInput(Unit) {
            detectDragGestures(
                onDrag = { change, dragAmount ->
                    change.consume()
                    val startPositionFromCenter = change.previousPosition - center
                    val endPositionFromCenter = startPositionFromCenter + dragAmount
                    val startPositionPolar = startPositionFromCenter.toPolarCoordinates()
                    val endPositionPolar = endPositionFromCenter.toPolarCoordinates()
                    val angleDiff = (endPositionPolar.angle - startPositionPolar.angle).toDegrees()
                    coroutineScope.launch { rotationState.rotateBy(angleDiff) }
                    lastVelocity = AngularVelocity(angleDiff / (change.uptimeMillis - change.previousUptimeMillis))
                },
                onDragEnd = {
                    coroutineScope.launch {
                        rotationState.onUserRotationFinished(lastVelocity)
                    }
                }
            )
        }.pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    if (rotationState.isRotationInProgress) {
                        coroutineScope.launch { rotationState.stopRotation() }
                    }
                }
            )
        }
    )
}
