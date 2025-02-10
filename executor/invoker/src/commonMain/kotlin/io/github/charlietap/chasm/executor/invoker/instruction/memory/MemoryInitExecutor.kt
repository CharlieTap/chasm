package io.github.charlietap.chasm.executor.invoker.instruction.memory

import io.github.charlietap.chasm.executor.memory.init.LinearMemoryInitialiser
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction

fun MemoryInitExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryInit,
) =
    MemoryInitExecutor(
        context = context,
        instruction = instruction,
        linearMemoryInitialiser = ::LinearMemoryInitialiser,
    )

internal inline fun MemoryInitExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryInit,
    crossinline linearMemoryInitialiser: LinearMemoryInitialiser,
) {
    val stack = context.vstack
    val memory = instruction.memory
    val data = instruction.data

    val bytesToCopy = stack.popI32()
    val sourceOffset = stack.popI32()
    val destinationOffset = stack.popI32()

    linearMemoryInitialiser(data.bytes, memory.data, sourceOffset, destinationOffset, bytesToCopy, data.bytes.size, memory.size)
}
