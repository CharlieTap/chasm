package io.github.charlietap.chasm.executor.invoker.instruction.memory

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.memory.init.LinearMemoryInitialiser
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
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
    val stack = context.stack

    val memory = instruction.memory
    val data = instruction.data

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
