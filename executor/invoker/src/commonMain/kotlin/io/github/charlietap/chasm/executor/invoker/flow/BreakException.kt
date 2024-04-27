package io.github.charlietap.chasm.executor.invoker.flow

import io.github.charlietap.chasm.executor.runtime.instruction.ExecutionInstruction
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

data class BreakException(
    val labelsToBreak: Int,
    val results: List<ExecutionValue>,
    val continuation: List<ExecutionInstruction>,
) : Exception()
