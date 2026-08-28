package io.github.charlietap.chasm.executor.invoker.instruction.memory

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

fun MemoryGrowExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryGrow,
) {
    val memory = instruction.memory
    val originalSizeInPages = memory.type.limits.min.toInt()

    val pagesToAdd = vstack.popI32()
    val newSizeInPages = originalSizeInPages + pagesToAdd

    if (newSizeInPages > instruction.max) {
        vstack.push(-1L)
    } else {

        memory.type.limits.min = newSizeInPages.toULong()
        memory.data = memory.data.grow(pagesToAdd)
        memory.refresh()

        vstack.pushI32(originalSizeInPages)
    }
}
