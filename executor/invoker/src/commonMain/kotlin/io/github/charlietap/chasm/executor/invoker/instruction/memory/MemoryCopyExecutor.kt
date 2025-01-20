package io.github.charlietap.chasm.executor.invoker.instruction.memory

import io.github.charlietap.chasm.executor.invoker.ext.bind
import io.github.charlietap.chasm.executor.memory.copy.LinearMemoryCopier
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.contains
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction

fun MemoryCopyExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryCopy,
) =
    MemoryCopyExecutor(
        context = context,
        instruction = instruction,
        copier = ::LinearMemoryCopier,
    )

internal inline fun MemoryCopyExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryCopy,
    crossinline copier: LinearMemoryCopier,
) {

    val stack = context.stack

    val srcMemory = instruction.srcMemory
    val dstMemory = instruction.dstMemory

    val bytesToCopy = stack.popI32()
    val sourceOffset = stack.popI32()
    val destinationOffset = stack.popI32()

    if (
        bytesToCopy < 0 ||
        !srcMemory.bounds.contains(sourceOffset..<sourceOffset + bytesToCopy) ||
        !dstMemory.bounds.contains(destinationOffset..<destinationOffset + bytesToCopy)
    ) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }

    if (bytesToCopy == 0) return

    copier(srcMemory.data, dstMemory.data, sourceOffset, destinationOffset, bytesToCopy).bind()
}
