package com.mariomg.circularlayouts.unit

import com.mariomg.circularlayouts.model.PI
import kotlin.math.abs
import kotlin.math.sign

@JvmInline
value class Radians(val value: Float) {
    fun toDegrees() = Degrees(value * 180 / PI)

    operator fun plus(other: Radians) = Radians(this.value + other.value)

    operator fun minus(other: Radians) = Radians(this.value - other.value)

    operator fun unaryMinus() = Radians(-this.value)

    operator fun times(number: Number) = Radians(this.value * number.toFloat())

    operator fun div(number: Number) = Radians(this.value / number.toFloat())

    operator fun div(other: Radians) = this.value / other.value

    operator fun rem(number: Number) = Radians(this.value % number.toFloat())

    operator fun rem(other: Radians) = Radians(this.value % other.value)

    operator fun compareTo(other: Radians) = this.value.compareTo(other.value)
}

operator fun Number.times(radians: Radians) = radians.times(this)

inline val Float.radians: Radians get() = Radians(this)

fun abs(radians: Radians) = abs(radians.value).radians

fun sign(radians: Radians) = sign(radians.value)
