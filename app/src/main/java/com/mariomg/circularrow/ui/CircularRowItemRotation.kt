package com.mariomg.circularrow.ui

import com.mariomg.circularrow.model.unit.Degrees
import com.mariomg.circularrow.model.unit.degrees

typealias Rotation = (Degrees) -> Degrees

enum class CircularRowItemRotation(private val rotation: Rotation) : Rotation {
    NONE(rotation = { 0.degrees }),
    TANGENT(rotation = { it }),
    PERPENDICULAR_CLOCKWISE(rotation = { it + 90.degrees }),
    PERPENDICULAR_COUNTERCLOCKWISE(rotation = { it - 90.degrees }),
    TANGENT_INVERSE(rotation = { it + 180.degrees }),
    ;

    override fun invoke(angle: Degrees) = rotation(angle)
}
