package io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.bulk

import io.github.charlietap.chasm.memory.copy.LinearMemoryCopier
import io.github.charlietap.chasm.memory.fill.LinearMemoryFiller
import io.github.charlietap.chasm.memory.grow.LinearMemoryGrower
import io.github.charlietap.chasm.memory.init.LinearMemoryInitialiser
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instance.DataInstance
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.instruction.FusedMemoryInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal fun MemoryGrowExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryGrowI,
) = MemoryGrowExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    grower = ::LinearMemoryGrower,
)

internal inline fun MemoryGrowExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryGrowI,
    crossinline grower: LinearMemoryGrower,
) = executeMemoryGrow(
    vstack = vstack,
    pagesToAdd = instruction.pagesToAdd,
    destinationSlot = instruction.destinationSlot,
    memory = instruction.memory,
    max = instruction.max,
    grower = grower,
)

internal fun MemoryGrowExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryGrowS,
) = MemoryGrowExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    grower = ::LinearMemoryGrower,
)

internal inline fun MemoryGrowExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryGrowS,
    crossinline grower: LinearMemoryGrower,
) = executeMemoryGrow(
    vstack = vstack,
    pagesToAdd = vstack.getFrameSlot(instruction.pagesToAddSlot).toInt(),
    destinationSlot = instruction.destinationSlot,
    memory = instruction.memory,
    max = instruction.max,
    grower = grower,
)

internal fun MemoryInitExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryInitIii,
) = MemoryInitExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    linearMemoryInitialiser = ::LinearMemoryInitialiser,
)

internal inline fun MemoryInitExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryInitIii,
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
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryInitIis,
) = MemoryInitExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    linearMemoryInitialiser = ::LinearMemoryInitialiser,
)

internal inline fun MemoryInitExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryInitIis,
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
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryInitIsi,
) = MemoryInitExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    linearMemoryInitialiser = ::LinearMemoryInitialiser,
)

internal inline fun MemoryInitExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryInitIsi,
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
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryInitIss,
) = MemoryInitExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    linearMemoryInitialiser = ::LinearMemoryInitialiser,
)

internal inline fun MemoryInitExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryInitIss,
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
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryInitSii,
) = MemoryInitExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    linearMemoryInitialiser = ::LinearMemoryInitialiser,
)

internal inline fun MemoryInitExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryInitSii,
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
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryInitSis,
) = MemoryInitExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    linearMemoryInitialiser = ::LinearMemoryInitialiser,
)

internal inline fun MemoryInitExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryInitSis,
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
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryInitSsi,
) = MemoryInitExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    linearMemoryInitialiser = ::LinearMemoryInitialiser,
)

internal inline fun MemoryInitExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryInitSsi,
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
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryInitSss,
) = MemoryInitExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    linearMemoryInitialiser = ::LinearMemoryInitialiser,
)

internal inline fun MemoryInitExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryInitSss,
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
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryCopyIii,
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
    instruction: FusedMemoryInstruction.MemoryCopyIii,
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
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryCopyIis,
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
    instruction: FusedMemoryInstruction.MemoryCopyIis,
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
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryCopyIsi,
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
    instruction: FusedMemoryInstruction.MemoryCopyIsi,
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
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryCopyIss,
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
    instruction: FusedMemoryInstruction.MemoryCopyIss,
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
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryCopySii,
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
    instruction: FusedMemoryInstruction.MemoryCopySii,
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
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryCopySis,
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
    instruction: FusedMemoryInstruction.MemoryCopySis,
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
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryCopySsi,
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
    instruction: FusedMemoryInstruction.MemoryCopySsi,
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
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryCopySss,
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
    instruction: FusedMemoryInstruction.MemoryCopySss,
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
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryFillIii,
) = MemoryFillExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    filler = ::LinearMemoryFiller,
)

internal inline fun MemoryFillExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryFillIii,
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
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryFillIis,
) = MemoryFillExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    filler = ::LinearMemoryFiller,
)

internal inline fun MemoryFillExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryFillIis,
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
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryFillIsi,
) = MemoryFillExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    filler = ::LinearMemoryFiller,
)

internal inline fun MemoryFillExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryFillIsi,
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
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryFillIss,
) = MemoryFillExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    filler = ::LinearMemoryFiller,
)

internal inline fun MemoryFillExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryFillIss,
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
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryFillSii,
) = MemoryFillExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    filler = ::LinearMemoryFiller,
)

internal inline fun MemoryFillExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryFillSii,
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
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryFillSis,
) = MemoryFillExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    filler = ::LinearMemoryFiller,
)

internal inline fun MemoryFillExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryFillSis,
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
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryFillSsi,
) = MemoryFillExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    filler = ::LinearMemoryFiller,
)

internal inline fun MemoryFillExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryFillSsi,
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
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryFillSss,
) = MemoryFillExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    filler = ::LinearMemoryFiller,
)

internal inline fun MemoryFillExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.MemoryFillSss,
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
    crossinline grower: LinearMemoryGrower,
) {
    val originalSizeInPages = memory.type.limits.min.toInt()
    val newSizeInPages = originalSizeInPages + pagesToAdd

    if (newSizeInPages > max) {
        vstack.setFrameSlot(destinationSlot, -1L)
    } else {
        memory.type.limits.min = newSizeInPages.toULong()
        memory.data = grower(memory.data, pagesToAdd)
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
