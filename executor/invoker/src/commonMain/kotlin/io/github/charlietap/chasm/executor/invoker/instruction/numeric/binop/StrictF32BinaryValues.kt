package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

// Shared scalar semantics for frame executors and generated Kotlin values.
internal inline fun valueF32Add(left: Float, right: Float): Float = left + right

internal inline fun valueF32Sub(left: Float, right: Float): Float = left - right

internal inline fun valueF32Mul(left: Float, right: Float): Float = left * right

internal inline fun valueF32Div(left: Float, right: Float): Float = left / right
