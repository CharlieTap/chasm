package io.github.charlietap.chasm.executor.invoker.instruction.memory

import io.github.charlietap.chasm.memory.grow.LinearMemoryGrower
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun MemoryGrowExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryGrow,
) =
    MemoryGrowExecutor(
        context = context,
        instruction = instruction,
        grower = ::LinearMemoryGrower,
    )

internal inline fun MemoryGrowExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryGrow,
    crossinline grower: LinearMemoryGrower,
) {
    val stack = context.vstack
    val memory = instruction.memory
    val originalSizeInPages = memory.type.limits.min.toInt()

    val pagesToAdd = stack.popI32()
    val newSizeInPages = originalSizeInPages + pagesToAdd

    if (newSizeInPages > instruction.max) {
        stack.push(-1L)
    } else {

        memory.type.limits.min = newSizeInPages.toUInt()
        memory.data = grower(memory.data, pagesToAdd)
        memory.refresh()

        stack.pushI32(originalSizeInPages)
    }
}
