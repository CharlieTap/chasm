package io.github.charlietap.chasm.executor.invoker.instruction.numeric.testop

// Shared scalar semantics for frame executors and generated Kotlin values.
internal inline fun valueI64Eqz(operand: Long): Long = if (operand == 0L) 1L else 0L
