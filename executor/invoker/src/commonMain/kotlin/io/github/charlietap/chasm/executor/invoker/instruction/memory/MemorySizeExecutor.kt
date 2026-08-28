package io.github.charlietap.chasm.executor.invoker.instruction.memory

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun MemorySizeExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemorySize,
) {
    val currentSizeInPages = instruction.memory.type.limits.min.toInt()

    vstack.pushI32(currentSizeInPages)
}
