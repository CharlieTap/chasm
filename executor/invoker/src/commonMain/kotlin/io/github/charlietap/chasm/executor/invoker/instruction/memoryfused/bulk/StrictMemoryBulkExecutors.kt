package io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.bulk

import io.github.charlietap.chasm.memory.copy.LinearMemoryCopier
import io.github.charlietap.chasm.memory.fill.LinearMemoryFiller
import io.github.charlietap.chasm.memory.grow.LinearMemoryGrower
import io.github.charlietap.chasm.memory.init.LinearMemoryInitialiser
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instance.DataInstance
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.instruction.MemorySuperInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal fun MemoryGrowExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemorySuperInstruction.MemoryGrowI,
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
    instruction: MemorySuperInstruction.MemoryGrowI,
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
    instruction: MemorySuperInstruction.MemoryGrowS,
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
    instruction: MemorySuperInstruction.MemoryGrowS,
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
    instruction: MemorySuperInstruction.MemoryInitIii,
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
    instruction: MemorySuperInstruction.MemoryInitIii,
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
    instruction: MemorySuperInstruction.MemoryInitIis,
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
    instruction: MemorySuperInstruction.MemoryInitIis,
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
    instruction: MemorySuperInstruction.MemoryInitIsi,
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
    instruction: MemorySuperInstruction.MemoryInitIsi,
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
    instruction: MemorySuperInstruction.MemoryInitIss,
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
    instruction: MemorySuperInstruction.MemoryInitIss,
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
    instruction: MemorySuperInstruction.MemoryInitSii,
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
    instruction: MemorySuperInstruction.MemoryInitSii,
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
    instruction: MemorySuperInstruction.MemoryInitSis,
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
    instruction: MemorySuperInstruction.MemoryInitSis,
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
    instruction: MemorySuperInstruction.MemoryInitSsi,
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
    instruction: MemorySuperInstruction.MemoryInitSsi,
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
    instruction: MemorySuperInstruction.MemoryInitSss,
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
    instruction: MemorySuperInstruction.MemoryInitSss,
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
    instruction: MemorySuperInstruction.MemoryCopyIii,
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
    instruction: MemorySuperInstruction.MemoryCopyIii,
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
    instruction: MemorySuperInstruction.MemoryCopyIis,
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
    instruction: MemorySuperInstruction.MemoryCopyIis,
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
    instruction: MemorySuperInstruction.MemoryCopyIsi,
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
    instruction: MemorySuperInstruction.MemoryCopyIsi,
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
    instruction: MemorySuperInstruction.MemoryCopyIss,
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
    instruction: MemorySuperInstruction.MemoryCopyIss,
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
    instruction: MemorySuperInstruction.MemoryCopySii,
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
    instruction: MemorySuperInstruction.MemoryCopySii,
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
    instruction: MemorySuperInstruction.MemoryCopySis,
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
    instruction: MemorySuperInstruction.MemoryCopySis,
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
    instruction: MemorySuperInstruction.MemoryCopySsi,
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
    instruction: MemorySuperInstruction.MemoryCopySsi,
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
    instruction: MemorySuperInstruction.MemoryCopySss,
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
    instruction: MemorySuperInstruction.MemoryCopySss,
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
    instruction: MemorySuperInstruction.MemoryFillIii,
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
    instruction: MemorySuperInstruction.MemoryFillIii,
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
    instruction: MemorySuperInstruction.MemoryFillIis,
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
    instruction: MemorySuperInstruction.MemoryFillIis,
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
    instruction: MemorySuperInstruction.MemoryFillIsi,
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
    instruction: MemorySuperInstruction.MemoryFillIsi,
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
    instruction: MemorySuperInstruction.MemoryFillIss,
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
    instruction: MemorySuperInstruction.MemoryFillIss,
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
    instruction: MemorySuperInstruction.MemoryFillSii,
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
    instruction: MemorySuperInstruction.MemoryFillSii,
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
    instruction: MemorySuperInstruction.MemoryFillSis,
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
    instruction: MemorySuperInstruction.MemoryFillSis,
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
    instruction: MemorySuperInstruction.MemoryFillSsi,
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
    instruction: MemorySuperInstruction.MemoryFillSsi,
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
    instruction: MemorySuperInstruction.MemoryFillSss,
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
    instruction: MemorySuperInstruction.MemoryFillSss,
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
