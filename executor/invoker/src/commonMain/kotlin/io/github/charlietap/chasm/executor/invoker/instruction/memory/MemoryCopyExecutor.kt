package io.github.charlietap.chasm.executor.invoker.instruction.memory

import io.github.charlietap.chasm.memory.copy.LinearMemoryCopier
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

fun MemoryCopyExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryCopy,
) = MemoryCopyExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    copier = ::LinearMemoryCopier,
)

internal inline fun MemoryCopyExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryCopy,
    crossinline copier: LinearMemoryCopier,
) {
    val srcMemory = instruction.srcMemory
    val dstMemory = instruction.dstMemory

    val bytesToCopy = vstack.popI32()
    val sourceOffset = vstack.popI32()
    val destinationOffset = vstack.popI32()

    copier(srcMemory.data, dstMemory.data, sourceOffset, destinationOffset, bytesToCopy, srcMemory.size, dstMemory.size)
}
