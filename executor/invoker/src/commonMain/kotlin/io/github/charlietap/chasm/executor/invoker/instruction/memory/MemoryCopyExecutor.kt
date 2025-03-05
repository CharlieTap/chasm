package io.github.charlietap.chasm.executor.invoker.instruction.memory

import io.github.charlietap.chasm.memory.copy.LinearMemoryCopier
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun MemoryCopyExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryCopy,
) = MemoryCopyExecutor(
    context = context,
    instruction = instruction,
    copier = ::LinearMemoryCopier,
)

internal inline fun MemoryCopyExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryCopy,
    crossinline copier: LinearMemoryCopier,
) {
    val stack = context.vstack
    val srcMemory = instruction.srcMemory
    val dstMemory = instruction.dstMemory

    val bytesToCopy = stack.popI32()
    val sourceOffset = stack.popI32()
    val destinationOffset = stack.popI32()

    copier(srcMemory.data, dstMemory.data, sourceOffset, destinationOffset, bytesToCopy, srcMemory.size, dstMemory.size)
}
