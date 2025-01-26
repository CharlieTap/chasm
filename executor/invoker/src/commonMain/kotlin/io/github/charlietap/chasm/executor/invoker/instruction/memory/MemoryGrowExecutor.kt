package io.github.charlietap.chasm.executor.invoker.instruction.memory

import io.github.charlietap.chasm.executor.memory.grow.LinearMemoryGrower
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

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

    val currentSizeInPages = memory.type.limits.min
        .toInt()
    val pagesToAdd = stack.popI32()

    val max = memory.type.limits.max
        ?.toInt() ?: LinearMemory.MAX_PAGES
    if (memory.type.limits.min
            .toInt() + pagesToAdd > max
    ) {
        stack.push(-1L)
    } else {

        memory.type.limits.min = memory.type.limits.min + pagesToAdd.toUInt()
        memory.data = grower(memory.data, pagesToAdd)
        memory.refresh()

        stack.pushI32(currentSizeInPages)
    }
}
