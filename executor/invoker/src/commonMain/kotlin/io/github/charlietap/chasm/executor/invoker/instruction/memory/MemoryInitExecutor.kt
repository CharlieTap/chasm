package io.github.charlietap.chasm.executor.invoker.instruction.memory

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.memory.init.LinearMemoryInitialiser
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.data
import io.github.charlietap.chasm.executor.runtime.ext.dataAddress
import io.github.charlietap.chasm.executor.runtime.ext.memory
import io.github.charlietap.chasm.executor.runtime.ext.memoryAddress
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction

fun MemoryInitExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryInit,
): Result<Unit, InvocationError> =
    MemoryInitExecutor(
        context = context,
        instruction = instruction,
        linearMemoryInitialiser = ::LinearMemoryInitialiser,
    )

internal inline fun MemoryInitExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryInit,
    crossinline linearMemoryInitialiser: LinearMemoryInitialiser,
): Result<Unit, InvocationError> = binding {
    val (stack, store) = context
    val frame = stack.peekFrame().bind()
    val memoryAddress = frame.state.module
        .memoryAddress(instruction.memoryIndex)
        .bind()
    val memory = store.memory(memoryAddress).bind()

    val dataAddress = frame.state.module
        .dataAddress(instruction.dataIndex)
        .bind()
    val data = store.data(dataAddress).bind()

    val bytesToCopy = stack.popI32().bind()
    val sourceOffset = stack.popI32().bind()
    val destinationOffset = stack.popI32().bind()

    if (
        bytesToCopy < 0 ||
        sourceOffset < 0 ||
        destinationOffset < 0 ||
        (sourceOffset + bytesToCopy) > data.bytes.size ||
        (destinationOffset + bytesToCopy) > memory.size
    ) {
        Err(InvocationError.Trap.TrapEncountered).bind<Unit>()
    }

    if (bytesToCopy == 0) {
        return@binding
    }

    linearMemoryInitialiser(data.bytes, memory.data, sourceOffset, destinationOffset, bytesToCopy).bind()
}
