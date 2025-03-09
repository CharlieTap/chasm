package io.github.charlietap.chasm.executor.invoker.instruction.memory

import io.github.charlietap.chasm.memory.grow.LinearMemoryGrower
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

fun MemoryGrowExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryGrow,
) = MemoryGrowExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    grower = ::LinearMemoryGrower,
)

internal inline fun MemoryGrowExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryGrow,
    crossinline grower: LinearMemoryGrower,
) {
    val memory = instruction.memory
    val originalSizeInPages = memory.type.limits.min.toInt()

    val pagesToAdd = vstack.popI32()
    val newSizeInPages = originalSizeInPages + pagesToAdd

    if (newSizeInPages > instruction.max) {
        vstack.push(-1L)
    } else {

        memory.type.limits.min = newSizeInPages.toUInt()
        memory.data = grower(memory.data, pagesToAdd)
        memory.refresh()

        vstack.pushI32(originalSizeInPages)
    }
}
