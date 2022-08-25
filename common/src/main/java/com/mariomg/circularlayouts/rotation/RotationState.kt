package com.mariomg.circularlayouts.rotation

import android.os.Parcelable
import androidx.compose.animation.core.*
import androidx.compose.foundation.MutatePriority
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import com.mariomg.circularlayouts.unit.*
import kotlinx.parcelize.Parcelize

class RotationState(
    initialAngularOffset: Degrees = 0.degrees,
    internal val rotatableFraction: Int = 0,
) : RotatableState {

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

    suspend fun rotateBy(angle: Degrees) {
        rotate {
            rotateBy(angle)
        }
    }

    suspend fun rotateTo(angularOffset: Degrees) = this.rotateBy(angularOffset - this.angularOffset)

    suspend fun animateRotateBy(
        angle: Degrees,
        animationSpec: AnimationSpec<Float> = tween(durationMillis = DEFAULT_ANIMATION_DURATION_MILLIS)
    ) {
        rotate {
            var previousValue = 0f
            animate(
                initialValue = 0f,
                targetValue = angle.value,
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
            angle = 360.degrees,
            animationSpec = infiniteRepeatable(animation = TweenSpec(
                durationMillis = durationMillisPerCycle,
                easing = LinearEasing,
            ))
        )

    suspend fun onRotationFinished() {
        if (rotatableFraction != 0) {
            val allowedRotationAngle = 360.degrees / rotatableFraction
            val remainder = abs(angularOffset % allowedRotationAngle)
            val angleToRotate = if (remainder < allowedRotationAngle / 2) -remainder
                else allowedRotationAngle - remainder
            animateRotateBy(sign(angularOffset) * angleToRotate, animationSpec = tween())
        }
    }

    companion object {
        private const val DEFAULT_ANIMATION_DURATION_MILLIS = 2000

        internal val Saver: Saver<RotationState, RotationStateData> = Saver(
            save = { it.toRotationStateData() },
            restore = { it.toRotationState() }
        )
    }
}

@Composable
fun rememberRotationState(
    initialAngularOffset: Degrees = 0.degrees,
    rotatableFraction: Int = 0,
): RotationState {
    return rememberSaveable(saver = RotationState.Saver) {
        RotationState(
            initialAngularOffset = initialAngularOffset,
            rotatableFraction = rotatableFraction,
        )
    }
}

@Parcelize
internal data class RotationStateData (
    val angularOffset: Float,
    val rotatableFraction: Int,
) : Parcelable {
    fun toRotationState() = RotationState(
        initialAngularOffset = angularOffset.degrees,
        rotatableFraction = rotatableFraction,
    )
}

private fun RotationState.toRotationStateData() = RotationStateData(
    angularOffset = angularOffset.value,
    rotatableFraction = rotatableFraction,
)
