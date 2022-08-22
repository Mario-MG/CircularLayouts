package com.mariomg.circularlayouts.model

import androidx.compose.ui.geometry.Offset
import com.mariomg.circularlayouts.unit.Degrees
import com.mariomg.circularlayouts.unit.Radians
import com.mariomg.circularlayouts.unit.radians
import kotlin.math.*
import kotlin.math.PI as PI_DOUBLE

const val PI = PI_DOUBLE.toFloat()

data class PolarCoordinates(
    val radius: Float,
    val angle: Radians,
) {
    fun toOffset() = Offset(
        x = radius * sin(angle.value),
        y = -radius * cos(angle.value),
    )

    companion object {
        fun usingDegrees(
            radius: Float,
            angle: Degrees,
        ) = PolarCoordinates(
            radius = radius,
            angle = angle.toRadians()
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
    }.radians,
)
