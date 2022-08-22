package com.mariomg.circularlayouts.rotation

import androidx.compose.animation.core.*
import androidx.compose.foundation.MutatePriority
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import com.mariomg.circularlayouts.unit.Degrees
import com.mariomg.circularlayouts.unit.degrees

class RotationState(initialAngularOffset: Degrees = 0.degrees) : RotatableState {

    private var _angularOffset by mutableStateOf(initialAngularOffset)
    var angularOffset: Degrees
        get() = _angularOffset
        private set(value) {
            if (value % 360 != _angularOffset) {
                _angularOffset = value % 360
            }
        }

    private val rotatableState = RotatableState {
        this.angularOffset += it
    }

    override suspend fun rotate(
        rotationPriority: MutatePriority,
        block: suspend RotationScope.() -> Unit
    ): Unit = rotatableState.rotate(rotationPriority, block)

    override fun dispatchRawDelta(delta: Degrees) {
        rotatableState.dispatchRawDelta(delta)
    }

    override val isRotationInProgress: Boolean
        get() = rotatableState.isRotationInProgress

    suspend fun rotateBy(angularOffset: Degrees) {
        rotate {
            rotateBy(angularOffset)
        }
    }

    suspend fun rotateTo(angularOffset: Degrees) = this.rotateBy(angularOffset - this.angularOffset)

    suspend fun animateRotateBy(
        angularOffset: Degrees,
        animationSpec: AnimationSpec<Float> = tween(durationMillis = DEFAULT_ANIMATION_DURATION_MILLIS)
    ) {
        rotate {
            var previousValue = 0f
            animate(
                initialValue = 0f,
                targetValue = angularOffset.value,
                animationSpec = animationSpec,
            ) { currentValue, _ ->
                val increment = currentValue - previousValue
                rotateBy(increment.degrees)
                previousValue += increment
            }
        }
    }

    suspend fun stopRotation() {
        rotate {
            rotateBy(0.degrees)
        }
    }

    suspend fun animateRotateTo(
        angularOffset: Degrees,
        animationSpec: AnimationSpec<Float> = tween(durationMillis = DEFAULT_ANIMATION_DURATION_MILLIS)
    ) = animateRotateBy(angularOffset - this.angularOffset, animationSpec)

    suspend fun rotateInfinitely(durationMillisPerCycle: Int = DEFAULT_ANIMATION_DURATION_MILLIS) =
        animateRotateBy(
            angularOffset = 360.degrees,
            animationSpec = infiniteRepeatable(animation = TweenSpec(
                durationMillis = durationMillisPerCycle,
                easing = LinearEasing,
            ))
        )

    companion object {
        private const val DEFAULT_ANIMATION_DURATION_MILLIS = 2000

        val Saver: Saver<RotationState, Float> = Saver(
            save = { it._angularOffset.value },
            restore = { RotationState(it.degrees) }
        )
    }
}

@Composable
fun rememberRotationState(initialAngularOffset: Degrees = 0.degrees): RotationState {
    return rememberSaveable(saver = RotationState.Saver) {
        RotationState(initialAngularOffset = initialAngularOffset)
    }
}