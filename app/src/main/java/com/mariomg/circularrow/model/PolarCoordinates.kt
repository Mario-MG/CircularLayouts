package com.mariomg.circularrow.model

import androidx.compose.ui.geometry.Offset
import com.mariomg.circularrow.model.unit.Degrees
import com.mariomg.circularrow.model.unit.Radians
import com.mariomg.circularrow.model.unit.radians
import kotlin.math.*
import kotlin.math.PI as PI_DOUBLE

const val PI = PI_DOUBLE.toFloat()

@JvmInline
value class Degrees(val value: Float) {
    fun toRadians() = Radians(value * PI / 180)
}

inline val Float.degrees: Degrees get() = Degrees(this)

@JvmInline
value class Radians(val value: Float) {
    fun toDegrees() = Degrees(value * 180 / PI)
}

inline val Float.radians: Radians get() = Radians(this)

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
