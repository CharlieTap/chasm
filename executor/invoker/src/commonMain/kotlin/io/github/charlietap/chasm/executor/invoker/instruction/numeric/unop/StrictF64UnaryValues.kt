package io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop

import kotlin.math.absoluteValue

// Shared scalar semantics for frame executors and generated Kotlin values.
internal inline fun valueF64Abs(operand: Double): Double = operand.absoluteValue

internal inline fun valueF64Neg(operand: Double): Double = -operand
