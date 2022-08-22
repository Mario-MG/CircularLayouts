package com.mariomg.circularlayouts.rotation

import androidx.compose.animation.core.*
import androidx.compose.foundation.MutatePriority
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable

class RotationState(initialAngularOffset: Float = 0f) : RotatableState {

    private var _angularOffset by mutableStateOf(initialAngularOffset)
    var angularOffset: Float
        get() = _angularOffset
        private set(value) {
            if (value % 360 != _angularOffset) {
                _angularOffset = value % 360
            }
        }

    private val rotatableState = RotatableState {
        this.angularOffset += it
        it
    }

    override suspend fun rotate(
        rotationPriority: MutatePriority,
        block: suspend RotationScope.() -> Unit
    ): Unit = rotatableState.rotate(rotationPriority, block)

    override fun dispatchRawDelta(delta: Float): Float =
        rotatableState.dispatchRawDelta(delta)

    override val isRotationInProgress: Boolean
        get() = rotatableState.isRotationInProgress

    suspend fun rotateBy(angularOffset: Float) {
        rotate {
            rotateBy(angularOffset)
        }
    }

    suspend fun rotateTo(angularOffset: Float) = this.rotateBy(angularOffset - this.angularOffset)

    suspend fun animateRotateBy(
        angularOffset: Float,
        animationSpec: AnimationSpec<Float> = tween(durationMillis = DEFAULT_ANIMATION_DURATION_MILLIS)
    ) {
        rotate {
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
    }

    suspend fun stopRotation() {
        rotate {
            rotateBy(0f)
        }
    }

    suspend fun animateRotateTo(
        angularOffset: Float,
        animationSpec: AnimationSpec<Float> = tween(durationMillis = DEFAULT_ANIMATION_DURATION_MILLIS)
    ) = animateRotateBy(angularOffset - this.angularOffset, animationSpec)

    suspend fun rotateInfinitely(durationMillisPerCycle: Int = DEFAULT_ANIMATION_DURATION_MILLIS) =
        animateRotateBy(
            angularOffset = 360f,
            animationSpec = infiniteRepeatable(animation = TweenSpec(
                durationMillis = durationMillisPerCycle,
                easing = LinearEasing,
            ))
        )

    companion object {
        private const val DEFAULT_ANIMATION_DURATION_MILLIS = 2000

        val Saver: Saver<RotationState, Float> = Saver(
            save = { it._angularOffset },
            restore = { RotationState(it) }
        )
    }
}

@Composable
fun rememberRotationState(initialAngularOffset: Float = 0f): RotationState {
    return rememberSaveable(saver = RotationState.Saver) {
        RotationState(initialAngularOffset = initialAngularOffset)
    }
}