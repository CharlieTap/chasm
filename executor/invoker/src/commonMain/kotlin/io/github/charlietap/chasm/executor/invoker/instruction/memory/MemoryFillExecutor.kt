package io.github.charlietap.chasm.executor.invoker.instruction.memory

import io.github.charlietap.chasm.executor.memory.fill.LinearMemoryFiller
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction

fun MemoryFillExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryFill,
) =
    MemoryFillExecutor(
        context = context,
        instruction = instruction,
        filler = ::LinearMemoryFiller,
    )

internal inline fun MemoryFillExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryFill,
    crossinline filler: LinearMemoryFiller,
) {
    val stack = context.vstack
    val memory = instruction.memory

    val bytesToFill = stack.popI32()
    val fillValue = stack.popI32()
    val offset = stack.popI32()

    filler(memory.data, offset, bytesToFill, fillValue.toByte(), memory.size)
}
