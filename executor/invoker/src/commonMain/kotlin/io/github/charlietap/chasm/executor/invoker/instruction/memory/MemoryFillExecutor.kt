package io.github.charlietap.chasm.executor.invoker.instruction.memory

import io.github.charlietap.chasm.memory.fill.LinearMemoryFiller
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

fun MemoryFillExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryFill,
) =
    MemoryFillExecutor(
        vstack = vstack,
        cstack = cstack,
        store = store,
        context = context,
        instruction = instruction,
        filler = ::LinearMemoryFiller,
    )

internal inline fun MemoryFillExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryFill,
    crossinline filler: LinearMemoryFiller,
) {
    val memory = instruction.memory

    val bytesToFill = vstack.popI32()
    val fillValue = vstack.popI32()
    val offset = vstack.popI32()

    filler(memory.data, offset, bytesToFill, fillValue.toByte(), memory.size)
}
