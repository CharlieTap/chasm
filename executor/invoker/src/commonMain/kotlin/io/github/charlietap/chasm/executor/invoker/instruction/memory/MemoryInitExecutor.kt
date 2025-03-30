package io.github.charlietap.chasm.executor.invoker.instruction.memory

import io.github.charlietap.chasm.memory.init.LinearMemoryInitialiser
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

fun MemoryInitExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryInit,
): InstructionPointer =
    MemoryInitExecutor(
        ip = ip,
        vstack = vstack,
        cstack = cstack,
        store = store,
        context = context,
        instruction = instruction,
        linearMemoryInitialiser = ::LinearMemoryInitialiser,
    )

internal inline fun MemoryInitExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryInit,
    crossinline linearMemoryInitialiser: LinearMemoryInitialiser,
): InstructionPointer {
    val memory = instruction.memory
    val data = instruction.data

    val bytesToCopy = vstack.popI32()
    val sourceOffset = vstack.popI32()
    val destinationOffset = vstack.popI32()

    linearMemoryInitialiser(data.bytes, memory.data, sourceOffset, destinationOffset, bytesToCopy, data.bytes.size, memory.size)
    return ip + 1
}
