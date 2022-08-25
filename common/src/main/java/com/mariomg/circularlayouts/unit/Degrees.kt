package com.mariomg.circularlayouts.unit

import com.mariomg.circularlayouts.model.PI
import kotlin.math.abs
import kotlin.math.sign

@JvmInline
value class Degrees(val value: Float) {
    fun toRadians() = Radians(value * PI / 180)

    operator fun plus(other: Degrees) = Degrees(this.value + other.value)

    operator fun minus(other: Degrees) = Degrees(this.value - other.value)

    operator fun unaryMinus() = Degrees(-this.value)

    operator fun times(number: Number) = Degrees(this.value * number.toFloat())

    operator fun div(number: Number) = Degrees(this.value / number.toFloat())

    operator fun div(other: Degrees) = this.value / other.value

    operator fun rem(number: Number) = Degrees(this.value % number.toFloat())

    operator fun rem(other: Degrees) = Degrees(this.value % other.value)

    operator fun compareTo(other: Degrees) = this.value.compareTo(other.value)
}

operator fun Number.times(degrees: Degrees) = degrees.times(this)

inline val Float.degrees: Degrees get() = Degrees(this)

inline val Int.degrees: Degrees get() = Degrees(this.toFloat())

fun abs(degrees: Degrees) = abs(degrees.value).degrees

fun sign(degrees: Degrees) = sign(degrees.value)
