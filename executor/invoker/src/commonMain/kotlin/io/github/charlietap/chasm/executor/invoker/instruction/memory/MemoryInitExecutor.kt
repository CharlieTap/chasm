package io.github.charlietap.chasm.executor.invoker.instruction.memory

import io.github.charlietap.chasm.memory.init.LinearMemoryInitialiser
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

fun MemoryInitExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryInit,
) =
    MemoryInitExecutor(
        vstack = vstack,
        context = context,
        instruction = instruction,
        linearMemoryInitialiser = ::LinearMemoryInitialiser,
    )

internal inline fun MemoryInitExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryInit,
    crossinline linearMemoryInitialiser: LinearMemoryInitialiser,
) {
    val memory = instruction.memory
    val data = instruction.data

    val bytesToCopy = vstack.popI32()
    val sourceOffset = vstack.popI32()
    val destinationOffset = vstack.popI32()

    linearMemoryInitialiser(data.bytes, memory.data, sourceOffset, destinationOffset, bytesToCopy, data.bytes.size, memory.size)
}
