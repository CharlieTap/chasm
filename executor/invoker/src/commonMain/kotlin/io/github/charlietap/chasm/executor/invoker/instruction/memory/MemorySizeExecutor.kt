package io.github.charlietap.chasm.executor.invoker.instruction.memory

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun MemorySizeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemorySize,
) {
    val currentSizeInPages = instruction.memory.type.limits.min.toInt()

    vstack.pushI32(currentSizeInPages)
}
