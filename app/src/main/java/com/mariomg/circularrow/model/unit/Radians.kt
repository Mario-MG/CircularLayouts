package com.mariomg.circularrow.model.unit

import com.mariomg.circularrow.model.PI

@JvmInline
value class Radians(val value: Float) {
    fun toDegrees() = Degrees(value * 180 / PI)

    operator fun plus(other: Radians) = Radians(this.value + other.value)

    operator fun minus(other: Radians) = Radians(this.value - other.value)

    operator fun times(number: Number) = Radians(this.value * number.toFloat())

    operator fun div(number: Number) = Radians(this.value / number.toFloat())

    operator fun compareTo(other: Radians) = this.value.compareTo(other.value)
}

operator fun Number.times(radians: Radians) = radians.times(this)

inline val Float.radians: Radians get() = Radians(this)
