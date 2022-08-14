package com.mariomg.circularrow.ui.composables

import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

class RotatableState(initialAngularOffset: Float = 0f) {

    private var _angularOffset by mutableStateOf(initialAngularOffset)
    var angularOffset: Float
        get() = _angularOffset
        private set(value) {
            if (value % 360 != _angularOffset) {
                _angularOffset = value % 360
            }
        }

    fun rotateBy(angularOffset: Float) {
        this.angularOffset += angularOffset
    }

    fun rotateTo(angularOffset: Float) = this.rotateBy(angularOffset - this.angularOffset)

    suspend fun animateRotateBy(
        angularOffset: Float,
        animationSpec: AnimationSpec<Float> = tween(durationMillis = DEFAULT_ANIMATION_DURATION_MILLIS)
    ) {
        var previousValue = 0f
        animate(
            initialValue = 0f,
            targetValue = angularOffset,
            animationSpec = animationSpec,
        ) { currentValue, _ ->
            val increment = currentValue - previousValue
            rotateBy(increment)
            previousValue += increment
        }
    }

    suspend fun animateRotateTo(
        angularOffset: Float,
        animationSpec: AnimationSpec<Float> = tween(durationMillis = DEFAULT_ANIMATION_DURATION_MILLIS)
    ) = animateRotateBy(angularOffset - this.angularOffset, animationSpec)

    suspend fun rotateInfinitely(durationMillisPerCycle: Int = DEFAULT_ANIMATION_DURATION_MILLIS) =
        animateRotateTo(
            angularOffset = 360f,
            animationSpec = infiniteRepeatable(animation = TweenSpec(
                durationMillis = durationMillisPerCycle,
                easing = LinearEasing,
            ))
        )

    companion object {
        private const val DEFAULT_ANIMATION_DURATION_MILLIS = 2000

        val Saver: Saver<RotatableState, Float> = Saver(
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