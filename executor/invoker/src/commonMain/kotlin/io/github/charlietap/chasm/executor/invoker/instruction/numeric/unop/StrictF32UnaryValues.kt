package io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop

import kotlin.math.absoluteValue

// Shared scalar semantics for frame executors and generated Kotlin values.
internal inline fun valueF32Abs(operand: Float): Float = operand.absoluteValue

internal inline fun valueF32Neg(operand: Float): Float = -operand
