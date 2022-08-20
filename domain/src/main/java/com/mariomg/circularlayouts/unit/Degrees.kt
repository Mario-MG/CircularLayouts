package com.mariomg.circularlayouts.unit

import com.mariomg.circularlayouts.model.PI

@JvmInline
value class Degrees(val value: Float) {
    fun toRadians() = Radians(value * PI / 180)

    operator fun plus(other: Degrees) = Degrees(this.value + other.value)

    operator fun minus(other: Degrees) = Degrees(this.value - other.value)

    operator fun times(number: Number) = Degrees(this.value * number.toFloat())

    operator fun div(number: Number) = Degrees(this.value / number.toFloat())

    operator fun compareTo(other: Degrees) = this.value.compareTo(other.value)
}

operator fun Number.times(degrees: Degrees) = degrees.times(this)

inline val Float.degrees: Degrees get() = Degrees(this)

inline val Int.degrees: Degrees get() = Degrees(this.toFloat())
