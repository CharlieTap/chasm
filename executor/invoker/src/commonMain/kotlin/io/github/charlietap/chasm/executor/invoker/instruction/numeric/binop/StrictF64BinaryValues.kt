package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

// Shared scalar semantics for frame executors and generated Kotlin values.
internal inline fun valueF64Add(left: Double, right: Double): Double = left + right

internal inline fun valueF64Sub(left: Double, right: Double): Double = left - right

internal inline fun valueF64Mul(left: Double, right: Double): Double = left * right

internal inline fun valueF64Div(left: Double, right: Double): Double = left / right
