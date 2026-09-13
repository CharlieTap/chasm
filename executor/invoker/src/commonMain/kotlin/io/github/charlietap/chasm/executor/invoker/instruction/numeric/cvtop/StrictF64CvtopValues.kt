package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop

// Shared scalar semantics for frame executors and generated Kotlin values.
internal inline fun valueF64PromoteF32(operand: Float): Double = operand.toDouble()
