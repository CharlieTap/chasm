package io.github.charlietap.chasm.executor.invoker.instruction.memory.bulk

import io.github.charlietap.chasm.memory.copy.LinearMemoryCopier
import io.github.charlietap.chasm.memory.fill.LinearMemoryFiller
import io.github.charlietap.chasm.memory.init.LinearMemoryInitialiser
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instance.DataInstance
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun executeMemoryGrow(
    vstack: ValueStack,
    context: ExecutionContext,
    pagesToAdd: Int,
    destinationSlot: Int,
    memory: MemoryInstance,
) {
    val previousSize = memory.grow(pagesToAdd)
    if (previousSize < 0 && context.store.config.limits.trapOnGrowFailure) {
        throw InvocationException(InvocationError.MemoryGrowFailed)
    }
    vstack.setFrameSlot(destinationSlot, previousSize.toLong())
}

internal fun MemoryInitExecutor(
    bytesToCopy: Int,
    sourceOffset: Int,
    destinationOffset: Int,
    memory: MemoryInstance,
    data: DataInstance,
) = MemoryInitExecutor(
    bytesToCopy = bytesToCopy,
    sourceOffset = sourceOffset,
    destinationOffset = destinationOffset,
    memory = memory,
    data = data,
    linearMemoryInitialiser = ::LinearMemoryInitialiser,
)

internal inline fun MemoryInitExecutor(
    bytesToCopy: Int,
    sourceOffset: Int,
    destinationOffset: Int,
    memory: MemoryInstance,
    data: DataInstance,
    crossinline linearMemoryInitialiser: LinearMemoryInitialiser,
) {
    linearMemoryInitialiser(data.bytes, memory.data, sourceOffset, destinationOffset, bytesToCopy, data.bytes.size, memory.size)
}

internal fun MemoryCopyExecutor(
    bytesToCopy: Int,
    sourceOffset: Int,
    destinationOffset: Int,
    srcMemory: MemoryInstance,
    dstMemory: MemoryInstance,
) = MemoryCopyExecutor(
    bytesToCopy = bytesToCopy,
    sourceOffset = sourceOffset,
    destinationOffset = destinationOffset,
    srcMemory = srcMemory,
    dstMemory = dstMemory,
    copier = ::LinearMemoryCopier,
)

internal inline fun MemoryCopyExecutor(
    bytesToCopy: Int,
    sourceOffset: Int,
    destinationOffset: Int,
    srcMemory: MemoryInstance,
    dstMemory: MemoryInstance,
    crossinline copier: LinearMemoryCopier,
) {
    copier(srcMemory.data, dstMemory.data, sourceOffset, destinationOffset, bytesToCopy, srcMemory.size, dstMemory.size)
}

internal fun MemoryFillExecutor(
    bytesToFill: Int,
    fillValue: Int,
    offset: Int,
    memory: MemoryInstance,
) = MemoryFillExecutor(
    bytesToFill = bytesToFill,
    fillValue = fillValue,
    offset = offset,
    memory = memory,
    filler = ::LinearMemoryFiller,
)

internal inline fun MemoryFillExecutor(
    bytesToFill: Int,
    fillValue: Int,
    offset: Int,
    memory: MemoryInstance,
    crossinline filler: LinearMemoryFiller,
) {
    filler(memory.data, offset, bytesToFill, fillValue.toByte(), memory.size)
}
