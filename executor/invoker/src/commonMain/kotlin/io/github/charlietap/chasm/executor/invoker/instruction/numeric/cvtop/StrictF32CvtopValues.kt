package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop

// Shared scalar semantics for frame executors and generated Kotlin values.
internal inline fun valueF32DemoteF64(operand: Double): Float = operand.toFloat()
