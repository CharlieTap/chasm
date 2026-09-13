package io.github.charlietap.chasm.executor.invoker.instruction.numeric.testop

// Shared scalar semantics for frame executors and generated Kotlin values.
internal inline fun valueI32Eqz(operand: Int): Int = if (operand == 0) 1 else 0
