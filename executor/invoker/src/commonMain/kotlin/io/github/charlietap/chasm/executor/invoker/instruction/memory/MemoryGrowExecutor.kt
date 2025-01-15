package io.github.charlietap.chasm.executor.invoker.instruction.memory

import io.github.charlietap.chasm.executor.invoker.ext.bind
import io.github.charlietap.chasm.executor.memory.grow.LinearMemoryGrower
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import io.github.charlietap.chasm.executor.runtime.value.NumberValue

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

    val stack = context.stack
    val memory = instruction.memory

    val currentSizeInPages = memory.type.limits.min
        .toInt()
    val pagesToAdd = stack.popI32().bind()

    val max = memory.type.limits.max
        ?.toInt() ?: LinearMemory.MAX_PAGES
    if (memory.type.limits.min
            .toInt() + pagesToAdd > max
    ) {
        stack.push(NumberValue.I32(-1))
    } else {

        memory.type.limits.min = memory.type.limits.min + pagesToAdd.toUInt()
        memory.data = grower(memory.data, pagesToAdd).bind()
        memory.refresh()

        stack.push(NumberValue.I32(currentSizeInPages))
    }
}
