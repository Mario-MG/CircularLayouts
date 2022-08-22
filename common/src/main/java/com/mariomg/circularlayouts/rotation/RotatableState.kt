package com.mariomg.circularlayouts.rotation

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.runtime.mutableStateOf
import com.mariomg.circularlayouts.unit.Degrees
import kotlinx.coroutines.coroutineScope

interface RotationScope {
    fun rotateBy(angle: Degrees)
}

interface RotatableState {
    suspend fun rotate(
        rotationPriority: MutatePriority = MutatePriority.Default,
        block: suspend RotationScope.() -> Unit
    )

    fun dispatchRawDelta(delta: Degrees)

    val isRotationInProgress: Boolean
}

private class DefaultRotatableState(val onDelta: (Degrees) -> Unit) : RotatableState {

    private val rotationScope: RotationScope = object : RotationScope {
        override fun rotateBy(angle: Degrees): Unit = onDelta(angle)
    }

    private val rotationMutex = MutatorMutex()

    private val isRotatingState = mutableStateOf(false)

    override suspend fun rotate(
        rotationPriority: MutatePriority,
        block: suspend RotationScope.() -> Unit
    ): Unit = coroutineScope {
        rotationMutex.mutateWith(rotationScope, rotationPriority) {
            isRotatingState.value = true
            try {
                block()
            } finally {
                isRotatingState.value = false
            }
        }
    }

    override fun dispatchRawDelta(delta: Degrees) {
        onDelta(delta)
    }

    override val isRotationInProgress: Boolean
        get() = isRotatingState.value
}

fun RotatableState(consumeRotationDelta: (Degrees) -> Unit): RotatableState {
    return DefaultRotatableState(consumeRotationDelta)
}
