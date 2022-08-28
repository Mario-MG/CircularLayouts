package com.mariomg.circularlayouts.model

import com.mariomg.circularlayouts.unit.Degrees

@JvmInline
value class AngularVelocity(
    val degreesPerMilli: Degrees,
) {
    constructor(degrees: Degrees, millis: Long): this(degrees / millis)
}
