package com.mariomg.circularrow.ui.composables

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.coroutineScope

interface RotationScope {
    fun rotateBy(angle: Float): Float
}

interface RotatableState {
    suspend fun rotate(
        rotationPriority: MutatePriority = MutatePriority.Default,
        block: suspend RotationScope.() -> Unit
    )

    fun dispatchRawDelta(delta: Float): Float

    val isRotationInProgress: Boolean
}

private class DefaultRotatableState(val onDelta: (Float) -> Float) : RotatableState {

    private val rotationScope: RotationScope = object : RotationScope {
        override fun rotateBy(angle: Float): Float = onDelta(angle)
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

    override fun dispatchRawDelta(delta: Float): Float {
        return onDelta(delta)
    }

    override val isRotationInProgress: Boolean
        get() = isRotatingState.value
}

fun RotatableState(consumeRotationDelta: (Float) -> Float): RotatableState {
    return DefaultRotatableState(consumeRotationDelta)
}
