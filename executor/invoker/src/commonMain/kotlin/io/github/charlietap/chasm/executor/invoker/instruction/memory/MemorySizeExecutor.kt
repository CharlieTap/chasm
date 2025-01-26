package io.github.charlietap.chasm.executor.invoker.instruction.memory

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction

internal inline fun MemorySizeExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.MemorySize,
) {
    val stack = context.vstack
    val currentSizeInPages = instruction.memory.type.limits.min
        .toInt()

    stack.pushI32(currentSizeInPages)
}
