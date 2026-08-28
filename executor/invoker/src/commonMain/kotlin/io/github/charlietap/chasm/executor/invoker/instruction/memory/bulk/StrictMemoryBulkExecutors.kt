package io.github.charlietap.chasm.executor.invoker.instruction.memory.bulk

import io.github.charlietap.chasm.memory.copy.LinearMemoryCopier
import io.github.charlietap.chasm.memory.fill.LinearMemoryFiller
import io.github.charlietap.chasm.memory.init.LinearMemoryInitialiser
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instance.DataInstance
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal fun MemoryGrowExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryGrowI,
) = executeMemoryGrow(
    vstack = vstack,
    pagesToAdd = instruction.pagesToAdd,
    destinationSlot = instruction.destinationSlot,
    memory = instruction.memory,
    max = instruction.max,
)

internal fun MemoryGrowExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryGrowS,
) = executeMemoryGrow(
    vstack = vstack,
    pagesToAdd = vstack.getFrameSlot(instruction.pagesToAddSlot).toInt(),
    destinationSlot = instruction.destinationSlot,
    memory = instruction.memory,
    max = instruction.max,
)

internal fun MemoryInitExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryInitIii,
) = MemoryInitExecutor(
    vstack = vstack,
    context = context,
    instruction = instruction,
    linearMemoryInitialiser = ::LinearMemoryInitialiser,
)

internal inline fun MemoryInitExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryInitIii,
    crossinline linearMemoryInitialiser: LinearMemoryInitialiser,
) = executeMemoryInit(
    bytesToCopy = instruction.bytesToCopy,
    sourceOffset = instruction.sourceOffset,
    destinationOffset = instruction.destinationOffset,
    memory = instruction.memory,
    data = instruction.data,
    linearMemoryInitialiser = linearMemoryInitialiser,
)

internal fun MemoryInitExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryInitIis,
) = MemoryInitExecutor(
    vstack = vstack,
    context = context,
    instruction = instruction,
    linearMemoryInitialiser = ::LinearMemoryInitialiser,
)

internal inline fun MemoryInitExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryInitIis,
    crossinline linearMemoryInitialiser: LinearMemoryInitialiser,
) = executeMemoryInit(
    bytesToCopy = instruction.bytesToCopy,
    sourceOffset = instruction.sourceOffset,
    destinationOffset = vstack.getFrameSlot(instruction.destinationOffsetSlot).toInt(),
    memory = instruction.memory,
    data = instruction.data,
    linearMemoryInitialiser = linearMemoryInitialiser,
)

internal fun MemoryInitExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryInitIsi,
) = MemoryInitExecutor(
    vstack = vstack,
    context = context,
    instruction = instruction,
    linearMemoryInitialiser = ::LinearMemoryInitialiser,
)

internal inline fun MemoryInitExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryInitIsi,
    crossinline linearMemoryInitialiser: LinearMemoryInitialiser,
) = executeMemoryInit(
    bytesToCopy = instruction.bytesToCopy,
    sourceOffset = vstack.getFrameSlot(instruction.sourceOffsetSlot).toInt(),
    destinationOffset = instruction.destinationOffset,
    memory = instruction.memory,
    data = instruction.data,
    linearMemoryInitialiser = linearMemoryInitialiser,
)

internal fun MemoryInitExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryInitIss,
) = MemoryInitExecutor(
    vstack = vstack,
    context = context,
    instruction = instruction,
    linearMemoryInitialiser = ::LinearMemoryInitialiser,
)

internal inline fun MemoryInitExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryInitIss,
    crossinline linearMemoryInitialiser: LinearMemoryInitialiser,
) = executeMemoryInit(
    bytesToCopy = instruction.bytesToCopy,
    sourceOffset = vstack.getFrameSlot(instruction.sourceOffsetSlot).toInt(),
    destinationOffset = vstack.getFrameSlot(instruction.destinationOffsetSlot).toInt(),
    memory = instruction.memory,
    data = instruction.data,
    linearMemoryInitialiser = linearMemoryInitialiser,
)

internal fun MemoryInitExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryInitSii,
) = MemoryInitExecutor(
    vstack = vstack,
    context = context,
    instruction = instruction,
    linearMemoryInitialiser = ::LinearMemoryInitialiser,
)

internal inline fun MemoryInitExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryInitSii,
    crossinline linearMemoryInitialiser: LinearMemoryInitialiser,
) = executeMemoryInit(
    bytesToCopy = vstack.getFrameSlot(instruction.bytesToCopySlot).toInt(),
    sourceOffset = instruction.sourceOffset,
    destinationOffset = instruction.destinationOffset,
    memory = instruction.memory,
    data = instruction.data,
    linearMemoryInitialiser = linearMemoryInitialiser,
)

internal fun MemoryInitExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryInitSis,
) = MemoryInitExecutor(
    vstack = vstack,
    context = context,
    instruction = instruction,
    linearMemoryInitialiser = ::LinearMemoryInitialiser,
)

internal inline fun MemoryInitExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryInitSis,
    crossinline linearMemoryInitialiser: LinearMemoryInitialiser,
) = executeMemoryInit(
    bytesToCopy = vstack.getFrameSlot(instruction.bytesToCopySlot).toInt(),
    sourceOffset = instruction.sourceOffset,
    destinationOffset = vstack.getFrameSlot(instruction.destinationOffsetSlot).toInt(),
    memory = instruction.memory,
    data = instruction.data,
    linearMemoryInitialiser = linearMemoryInitialiser,
)

internal fun MemoryInitExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryInitSsi,
) = MemoryInitExecutor(
    vstack = vstack,
    context = context,
    instruction = instruction,
    linearMemoryInitialiser = ::LinearMemoryInitialiser,
)

internal inline fun MemoryInitExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryInitSsi,
    crossinline linearMemoryInitialiser: LinearMemoryInitialiser,
) = executeMemoryInit(
    bytesToCopy = vstack.getFrameSlot(instruction.bytesToCopySlot).toInt(),
    sourceOffset = vstack.getFrameSlot(instruction.sourceOffsetSlot).toInt(),
    destinationOffset = instruction.destinationOffset,
    memory = instruction.memory,
    data = instruction.data,
    linearMemoryInitialiser = linearMemoryInitialiser,
)

internal fun MemoryInitExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryInitSss,
) = MemoryInitExecutor(
    vstack = vstack,
    context = context,
    instruction = instruction,
    linearMemoryInitialiser = ::LinearMemoryInitialiser,
)

internal inline fun MemoryInitExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryInitSss,
    crossinline linearMemoryInitialiser: LinearMemoryInitialiser,
) = executeMemoryInit(
    bytesToCopy = vstack.getFrameSlot(instruction.bytesToCopySlot).toInt(),
    sourceOffset = vstack.getFrameSlot(instruction.sourceOffsetSlot).toInt(),
    destinationOffset = vstack.getFrameSlot(instruction.destinationOffsetSlot).toInt(),
    memory = instruction.memory,
    data = instruction.data,
    linearMemoryInitialiser = linearMemoryInitialiser,
)

internal fun MemoryCopyExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryCopyIii,
) = MemoryCopyExecutor(
    vstack = vstack,
    context = context,
    instruction = instruction,
    copier = ::LinearMemoryCopier,
)

internal inline fun MemoryCopyExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryCopyIii,
    crossinline copier: LinearMemoryCopier,
) = executeMemoryCopy(
    bytesToCopy = instruction.bytesToCopy,
    sourceOffset = instruction.sourceOffset,
    destinationOffset = instruction.destinationOffset,
    srcMemory = instruction.srcMemory,
    dstMemory = instruction.dstMemory,
    copier = copier,
)

internal fun MemoryCopyExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryCopyIis,
) = MemoryCopyExecutor(
    vstack = vstack,
    context = context,
    instruction = instruction,
    copier = ::LinearMemoryCopier,
)

internal inline fun MemoryCopyExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryCopyIis,
    crossinline copier: LinearMemoryCopier,
) = executeMemoryCopy(
    bytesToCopy = instruction.bytesToCopy,
    sourceOffset = instruction.sourceOffset,
    destinationOffset = vstack.getFrameSlot(instruction.destinationOffsetSlot).toInt(),
    srcMemory = instruction.srcMemory,
    dstMemory = instruction.dstMemory,
    copier = copier,
)

internal fun MemoryCopyExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryCopyIsi,
) = MemoryCopyExecutor(
    vstack = vstack,
    context = context,
    instruction = instruction,
    copier = ::LinearMemoryCopier,
)

internal inline fun MemoryCopyExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryCopyIsi,
    crossinline copier: LinearMemoryCopier,
) = executeMemoryCopy(
    bytesToCopy = instruction.bytesToCopy,
    sourceOffset = vstack.getFrameSlot(instruction.sourceOffsetSlot).toInt(),
    destinationOffset = instruction.destinationOffset,
    srcMemory = instruction.srcMemory,
    dstMemory = instruction.dstMemory,
    copier = copier,
)

internal fun MemoryCopyExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryCopyIss,
) = MemoryCopyExecutor(
    vstack = vstack,
    context = context,
    instruction = instruction,
    copier = ::LinearMemoryCopier,
)

internal inline fun MemoryCopyExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryCopyIss,
    crossinline copier: LinearMemoryCopier,
) = executeMemoryCopy(
    bytesToCopy = instruction.bytesToCopy,
    sourceOffset = vstack.getFrameSlot(instruction.sourceOffsetSlot).toInt(),
    destinationOffset = vstack.getFrameSlot(instruction.destinationOffsetSlot).toInt(),
    srcMemory = instruction.srcMemory,
    dstMemory = instruction.dstMemory,
    copier = copier,
)

internal fun MemoryCopyExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryCopySii,
) = MemoryCopyExecutor(
    vstack = vstack,
    context = context,
    instruction = instruction,
    copier = ::LinearMemoryCopier,
)

internal inline fun MemoryCopyExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryCopySii,
    crossinline copier: LinearMemoryCopier,
) = executeMemoryCopy(
    bytesToCopy = vstack.getFrameSlot(instruction.bytesToCopySlot).toInt(),
    sourceOffset = instruction.sourceOffset,
    destinationOffset = instruction.destinationOffset,
    srcMemory = instruction.srcMemory,
    dstMemory = instruction.dstMemory,
    copier = copier,
)

internal fun MemoryCopyExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryCopySis,
) = MemoryCopyExecutor(
    vstack = vstack,
    context = context,
    instruction = instruction,
    copier = ::LinearMemoryCopier,
)

internal inline fun MemoryCopyExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryCopySis,
    crossinline copier: LinearMemoryCopier,
) = executeMemoryCopy(
    bytesToCopy = vstack.getFrameSlot(instruction.bytesToCopySlot).toInt(),
    sourceOffset = instruction.sourceOffset,
    destinationOffset = vstack.getFrameSlot(instruction.destinationOffsetSlot).toInt(),
    srcMemory = instruction.srcMemory,
    dstMemory = instruction.dstMemory,
    copier = copier,
)

internal fun MemoryCopyExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryCopySsi,
) = MemoryCopyExecutor(
    vstack = vstack,
    context = context,
    instruction = instruction,
    copier = ::LinearMemoryCopier,
)

internal inline fun MemoryCopyExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryCopySsi,
    crossinline copier: LinearMemoryCopier,
) = executeMemoryCopy(
    bytesToCopy = vstack.getFrameSlot(instruction.bytesToCopySlot).toInt(),
    sourceOffset = vstack.getFrameSlot(instruction.sourceOffsetSlot).toInt(),
    destinationOffset = instruction.destinationOffset,
    srcMemory = instruction.srcMemory,
    dstMemory = instruction.dstMemory,
    copier = copier,
)

internal fun MemoryCopyExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryCopySss,
) = MemoryCopyExecutor(
    vstack = vstack,
    context = context,
    instruction = instruction,
    copier = ::LinearMemoryCopier,
)

internal inline fun MemoryCopyExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryCopySss,
    crossinline copier: LinearMemoryCopier,
) = executeMemoryCopy(
    bytesToCopy = vstack.getFrameSlot(instruction.bytesToCopySlot).toInt(),
    sourceOffset = vstack.getFrameSlot(instruction.sourceOffsetSlot).toInt(),
    destinationOffset = vstack.getFrameSlot(instruction.destinationOffsetSlot).toInt(),
    srcMemory = instruction.srcMemory,
    dstMemory = instruction.dstMemory,
    copier = copier,
)

internal fun MemoryFillExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryFillIii,
) = MemoryFillExecutor(
    vstack = vstack,
    context = context,
    instruction = instruction,
    filler = ::LinearMemoryFiller,
)

internal inline fun MemoryFillExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryFillIii,
    crossinline filler: LinearMemoryFiller,
) = executeMemoryFill(
    bytesToFill = instruction.bytesToFill,
    fillValue = instruction.fillValue,
    offset = instruction.offset,
    memory = instruction.memory,
    filler = filler,
)

internal fun MemoryFillExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryFillIis,
) = MemoryFillExecutor(
    vstack = vstack,
    context = context,
    instruction = instruction,
    filler = ::LinearMemoryFiller,
)

internal inline fun MemoryFillExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryFillIis,
    crossinline filler: LinearMemoryFiller,
) = executeMemoryFill(
    bytesToFill = instruction.bytesToFill,
    fillValue = instruction.fillValue,
    offset = vstack.getFrameSlot(instruction.offsetSlot).toInt(),
    memory = instruction.memory,
    filler = filler,
)

internal fun MemoryFillExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryFillIsi,
) = MemoryFillExecutor(
    vstack = vstack,
    context = context,
    instruction = instruction,
    filler = ::LinearMemoryFiller,
)

internal inline fun MemoryFillExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryFillIsi,
    crossinline filler: LinearMemoryFiller,
) = executeMemoryFill(
    bytesToFill = instruction.bytesToFill,
    fillValue = vstack.getFrameSlot(instruction.fillValueSlot).toInt(),
    offset = instruction.offset,
    memory = instruction.memory,
    filler = filler,
)

internal fun MemoryFillExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryFillIss,
) = MemoryFillExecutor(
    vstack = vstack,
    context = context,
    instruction = instruction,
    filler = ::LinearMemoryFiller,
)

internal inline fun MemoryFillExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryFillIss,
    crossinline filler: LinearMemoryFiller,
) = executeMemoryFill(
    bytesToFill = instruction.bytesToFill,
    fillValue = vstack.getFrameSlot(instruction.fillValueSlot).toInt(),
    offset = vstack.getFrameSlot(instruction.offsetSlot).toInt(),
    memory = instruction.memory,
    filler = filler,
)

internal fun MemoryFillExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryFillSii,
) = MemoryFillExecutor(
    vstack = vstack,
    context = context,
    instruction = instruction,
    filler = ::LinearMemoryFiller,
)

internal inline fun MemoryFillExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryFillSii,
    crossinline filler: LinearMemoryFiller,
) = executeMemoryFill(
    bytesToFill = vstack.getFrameSlot(instruction.bytesToFillSlot).toInt(),
    fillValue = instruction.fillValue,
    offset = instruction.offset,
    memory = instruction.memory,
    filler = filler,
)

internal fun MemoryFillExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryFillSis,
) = MemoryFillExecutor(
    vstack = vstack,
    context = context,
    instruction = instruction,
    filler = ::LinearMemoryFiller,
)

internal inline fun MemoryFillExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryFillSis,
    crossinline filler: LinearMemoryFiller,
) = executeMemoryFill(
    bytesToFill = vstack.getFrameSlot(instruction.bytesToFillSlot).toInt(),
    fillValue = instruction.fillValue,
    offset = vstack.getFrameSlot(instruction.offsetSlot).toInt(),
    memory = instruction.memory,
    filler = filler,
)

internal fun MemoryFillExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryFillSsi,
) = MemoryFillExecutor(
    vstack = vstack,
    context = context,
    instruction = instruction,
    filler = ::LinearMemoryFiller,
)

internal inline fun MemoryFillExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryFillSsi,
    crossinline filler: LinearMemoryFiller,
) = executeMemoryFill(
    bytesToFill = vstack.getFrameSlot(instruction.bytesToFillSlot).toInt(),
    fillValue = vstack.getFrameSlot(instruction.fillValueSlot).toInt(),
    offset = instruction.offset,
    memory = instruction.memory,
    filler = filler,
)

internal fun MemoryFillExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryFillSss,
) = MemoryFillExecutor(
    vstack = vstack,
    context = context,
    instruction = instruction,
    filler = ::LinearMemoryFiller,
)

internal inline fun MemoryFillExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryFillSss,
    crossinline filler: LinearMemoryFiller,
) = executeMemoryFill(
    bytesToFill = vstack.getFrameSlot(instruction.bytesToFillSlot).toInt(),
    fillValue = vstack.getFrameSlot(instruction.fillValueSlot).toInt(),
    offset = vstack.getFrameSlot(instruction.offsetSlot).toInt(),
    memory = instruction.memory,
    filler = filler,
)

private inline fun executeMemoryGrow(
    vstack: ValueStack,
    pagesToAdd: Int,
    destinationSlot: Int,
    memory: MemoryInstance,
    max: Int,
) {
    val originalSizeInPages = memory.type.limits.min.toInt()
    val newSizeInPages = originalSizeInPages + pagesToAdd

    if (newSizeInPages > max) {
        vstack.setFrameSlot(destinationSlot, -1L)
    } else {
        memory.type.limits.min = newSizeInPages.toULong()
        memory.data = memory.data.grow(pagesToAdd)
        memory.refresh()
        vstack.setFrameSlot(destinationSlot, originalSizeInPages.toLong())
    }
}

private inline fun executeMemoryInit(
    bytesToCopy: Int,
    sourceOffset: Int,
    destinationOffset: Int,
    memory: MemoryInstance,
    data: DataInstance,
    crossinline linearMemoryInitialiser: LinearMemoryInitialiser,
) {
    linearMemoryInitialiser(data.bytes, memory.data, sourceOffset, destinationOffset, bytesToCopy, data.bytes.size, memory.size)
}

private inline fun executeMemoryCopy(
    bytesToCopy: Int,
    sourceOffset: Int,
    destinationOffset: Int,
    srcMemory: MemoryInstance,
    dstMemory: MemoryInstance,
    crossinline copier: LinearMemoryCopier,
) {
    copier(srcMemory.data, dstMemory.data, sourceOffset, destinationOffset, bytesToCopy, srcMemory.size, dstMemory.size)
}

private inline fun executeMemoryFill(
    bytesToFill: Int,
    fillValue: Int,
    offset: Int,
    memory: MemoryInstance,
    crossinline filler: LinearMemoryFiller,
) {
    filler(memory.data, offset, bytesToFill, fillValue.toByte(), memory.size)
}
