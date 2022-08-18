package com.mariomg.circularrow.model

import androidx.compose.ui.geometry.Offset
import kotlin.math.*
import kotlin.math.PI as PI_DOUBLE

const val PI = PI_DOUBLE.toFloat()

data class PolarCoordinates(
    val radius: Float,
    val angle: Float,
) {
    fun toOffset() = Offset(
        x = radius * sin(angle),
        y = -radius * cos(angle),
    )

    companion object {
        fun usingDegrees(
            radius: Float,
            angleInDeg: Float,
        ) = PolarCoordinates(
            radius = radius,
            angle = angleInDeg.degToRad()
        )
    }
}

fun Offset.toPolarCoordinates() = PolarCoordinates(
    radius = sqrt(x * x + y * y),
    angle = when {
        x >= 0 && y < 0 -> atan(-x / y)
        x >= 0 && y >= 0 -> atan(y / x) + PI / 2
        x < 0 && y >= 0 -> atan(-x / y) + PI
        else -> atan(y / x) + 3 * PI / 2
    },
)

fun Float.degToRad() = this * PI / 180

fun Float.radToDeg() = this * 180 / PI
