package io.github.charlietap.chasm.executor.invoker.instruction.tablefused

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.element
import io.github.charlietap.chasm.runtime.instance.ElementInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.instruction.FusedTableInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal fun TableCopyExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableCopyIii,
) = executeTableCopy(
    srcTable = instruction.srcTable,
    destTable = instruction.destTable,
    elementsToCopy = instruction.elementsToCopy,
    srcOffset = instruction.srcOffset,
    dstOffset = instruction.dstOffset,
)

internal fun TableCopyExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableCopyIis,
) = executeTableCopy(
    srcTable = instruction.srcTable,
    destTable = instruction.destTable,
    elementsToCopy = instruction.elementsToCopy,
    srcOffset = instruction.srcOffset,
    dstOffset = vstack.getFrameSlot(instruction.dstOffsetSlot).toInt(),
)

internal fun TableCopyExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableCopyIsi,
) = executeTableCopy(
    srcTable = instruction.srcTable,
    destTable = instruction.destTable,
    elementsToCopy = instruction.elementsToCopy,
    srcOffset = vstack.getFrameSlot(instruction.srcOffsetSlot).toInt(),
    dstOffset = instruction.dstOffset,
)

internal fun TableCopyExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableCopyIss,
) = executeTableCopy(
    srcTable = instruction.srcTable,
    destTable = instruction.destTable,
    elementsToCopy = instruction.elementsToCopy,
    srcOffset = vstack.getFrameSlot(instruction.srcOffsetSlot).toInt(),
    dstOffset = vstack.getFrameSlot(instruction.dstOffsetSlot).toInt(),
)

internal fun TableCopyExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableCopySii,
) = executeTableCopy(
    srcTable = instruction.srcTable,
    destTable = instruction.destTable,
    elementsToCopy = vstack.getFrameSlot(instruction.elementsToCopySlot).toInt(),
    srcOffset = instruction.srcOffset,
    dstOffset = instruction.dstOffset,
)

internal fun TableCopyExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableCopySis,
) = executeTableCopy(
    srcTable = instruction.srcTable,
    destTable = instruction.destTable,
    elementsToCopy = vstack.getFrameSlot(instruction.elementsToCopySlot).toInt(),
    srcOffset = instruction.srcOffset,
    dstOffset = vstack.getFrameSlot(instruction.dstOffsetSlot).toInt(),
)

internal fun TableCopyExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableCopySsi,
) = executeTableCopy(
    srcTable = instruction.srcTable,
    destTable = instruction.destTable,
    elementsToCopy = vstack.getFrameSlot(instruction.elementsToCopySlot).toInt(),
    srcOffset = vstack.getFrameSlot(instruction.srcOffsetSlot).toInt(),
    dstOffset = instruction.dstOffset,
)

internal fun TableCopyExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableCopySss,
) = executeTableCopy(
    srcTable = instruction.srcTable,
    destTable = instruction.destTable,
    elementsToCopy = vstack.getFrameSlot(instruction.elementsToCopySlot).toInt(),
    srcOffset = vstack.getFrameSlot(instruction.srcOffsetSlot).toInt(),
    dstOffset = vstack.getFrameSlot(instruction.dstOffsetSlot).toInt(),
)

internal fun TableFillExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableFillIii,
) = executeTableFill(
    table = instruction.table,
    elementsToFill = instruction.elementsToFill,
    fillValue = instruction.fillValue,
    tableOffset = instruction.tableOffset,
)

internal fun TableFillExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableFillIis,
) = executeTableFill(
    table = instruction.table,
    elementsToFill = instruction.elementsToFill,
    fillValue = instruction.fillValue,
    tableOffset = vstack.getFrameSlot(instruction.tableOffsetSlot).toInt(),
)

internal fun TableFillExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableFillIsi,
) = executeTableFill(
    table = instruction.table,
    elementsToFill = instruction.elementsToFill,
    fillValue = vstack.getFrameSlot(instruction.fillValueSlot),
    tableOffset = instruction.tableOffset,
)

internal fun TableFillExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableFillIss,
) = executeTableFill(
    table = instruction.table,
    elementsToFill = instruction.elementsToFill,
    fillValue = vstack.getFrameSlot(instruction.fillValueSlot),
    tableOffset = vstack.getFrameSlot(instruction.tableOffsetSlot).toInt(),
)

internal fun TableFillExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableFillSii,
) = executeTableFill(
    table = instruction.table,
    elementsToFill = vstack.getFrameSlot(instruction.elementsToFillSlot).toInt(),
    fillValue = instruction.fillValue,
    tableOffset = instruction.tableOffset,
)

internal fun TableFillExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableFillSis,
) = executeTableFill(
    table = instruction.table,
    elementsToFill = vstack.getFrameSlot(instruction.elementsToFillSlot).toInt(),
    fillValue = instruction.fillValue,
    tableOffset = vstack.getFrameSlot(instruction.tableOffsetSlot).toInt(),
)

internal fun TableFillExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableFillSsi,
) = executeTableFill(
    table = instruction.table,
    elementsToFill = vstack.getFrameSlot(instruction.elementsToFillSlot).toInt(),
    fillValue = vstack.getFrameSlot(instruction.fillValueSlot),
    tableOffset = instruction.tableOffset,
)

internal fun TableFillExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableFillSss,
) = executeTableFill(
    table = instruction.table,
    elementsToFill = vstack.getFrameSlot(instruction.elementsToFillSlot).toInt(),
    fillValue = vstack.getFrameSlot(instruction.fillValueSlot),
    tableOffset = vstack.getFrameSlot(instruction.tableOffsetSlot).toInt(),
)

internal fun TableGrowExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableGrowIi,
) = executeTableGrow(
    vstack = vstack,
    table = instruction.table,
    elementsToAdd = instruction.elementsToAdd,
    referenceValue = instruction.referenceValue,
    destinationSlot = instruction.destinationSlot,
    max = instruction.max,
)

internal fun TableGrowExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableGrowIs,
) = executeTableGrow(
    vstack = vstack,
    table = instruction.table,
    elementsToAdd = instruction.elementsToAdd,
    referenceValue = vstack.getFrameSlot(instruction.referenceValueSlot),
    destinationSlot = instruction.destinationSlot,
    max = instruction.max,
)

internal fun TableGrowExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableGrowSi,
) = executeTableGrow(
    vstack = vstack,
    table = instruction.table,
    elementsToAdd = vstack.getFrameSlot(instruction.elementsToAddSlot).toInt(),
    referenceValue = instruction.referenceValue,
    destinationSlot = instruction.destinationSlot,
    max = instruction.max,
)

internal fun TableGrowExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableGrowSs,
) = executeTableGrow(
    vstack = vstack,
    table = instruction.table,
    elementsToAdd = vstack.getFrameSlot(instruction.elementsToAddSlot).toInt(),
    referenceValue = vstack.getFrameSlot(instruction.referenceValueSlot),
    destinationSlot = instruction.destinationSlot,
    max = instruction.max,
)

internal fun TableInitExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableInitIii,
) = executeTableInit(
    table = instruction.table,
    element = instruction.element,
    elementsToInitialise = instruction.elementsToInitialise,
    segmentOffset = instruction.segmentOffset,
    tableOffset = instruction.tableOffset,
)

internal fun TableInitExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableInitIis,
) = executeTableInit(
    table = instruction.table,
    element = instruction.element,
    elementsToInitialise = instruction.elementsToInitialise,
    segmentOffset = instruction.segmentOffset,
    tableOffset = vstack.getFrameSlot(instruction.tableOffsetSlot).toInt(),
)

internal fun TableInitExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableInitIsi,
) = executeTableInit(
    table = instruction.table,
    element = instruction.element,
    elementsToInitialise = instruction.elementsToInitialise,
    segmentOffset = vstack.getFrameSlot(instruction.segmentOffsetSlot).toInt(),
    tableOffset = instruction.tableOffset,
)

internal fun TableInitExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableInitIss,
) = executeTableInit(
    table = instruction.table,
    element = instruction.element,
    elementsToInitialise = instruction.elementsToInitialise,
    segmentOffset = vstack.getFrameSlot(instruction.segmentOffsetSlot).toInt(),
    tableOffset = vstack.getFrameSlot(instruction.tableOffsetSlot).toInt(),
)

internal fun TableInitExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableInitSii,
) = executeTableInit(
    table = instruction.table,
    element = instruction.element,
    elementsToInitialise = vstack.getFrameSlot(instruction.elementsToInitialiseSlot).toInt(),
    segmentOffset = instruction.segmentOffset,
    tableOffset = instruction.tableOffset,
)

internal fun TableInitExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableInitSis,
) = executeTableInit(
    table = instruction.table,
    element = instruction.element,
    elementsToInitialise = vstack.getFrameSlot(instruction.elementsToInitialiseSlot).toInt(),
    segmentOffset = instruction.segmentOffset,
    tableOffset = vstack.getFrameSlot(instruction.tableOffsetSlot).toInt(),
)

internal fun TableInitExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableInitSsi,
) = executeTableInit(
    table = instruction.table,
    element = instruction.element,
    elementsToInitialise = vstack.getFrameSlot(instruction.elementsToInitialiseSlot).toInt(),
    segmentOffset = vstack.getFrameSlot(instruction.segmentOffsetSlot).toInt(),
    tableOffset = instruction.tableOffset,
)

internal fun TableInitExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableInitSss,
) = executeTableInit(
    table = instruction.table,
    element = instruction.element,
    elementsToInitialise = vstack.getFrameSlot(instruction.elementsToInitialiseSlot).toInt(),
    segmentOffset = vstack.getFrameSlot(instruction.segmentOffsetSlot).toInt(),
    tableOffset = vstack.getFrameSlot(instruction.tableOffsetSlot).toInt(),
)

internal inline fun TableGetExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableGetI,
) = executeTableGet(
    vstack = vstack,
    table = instruction.table,
    elementIndex = instruction.elementIndex,
    destinationSlot = instruction.destinationSlot,
)

internal inline fun TableGetExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableGetS,
) = executeTableGet(
    vstack = vstack,
    table = instruction.table,
    elementIndex = vstack.getFrameSlot(instruction.elementIndexSlot).toInt(),
    destinationSlot = instruction.destinationSlot,
)

internal inline fun TableSetExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableSetIi,
) = executeTableSet(
    table = instruction.table,
    elementIndex = instruction.elementIndex,
    value = instruction.value,
)

internal inline fun TableSetExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableSetIs,
) = executeTableSet(
    table = instruction.table,
    elementIndex = vstack.getFrameSlot(instruction.elementIndexSlot).toInt(),
    value = instruction.value,
)

internal inline fun TableSetExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableSetSi,
) = executeTableSet(
    table = instruction.table,
    elementIndex = instruction.elementIndex,
    value = vstack.getFrameSlot(instruction.valueSlot),
)

internal inline fun TableSetExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableSetSs,
) = executeTableSet(
    table = instruction.table,
    elementIndex = vstack.getFrameSlot(instruction.elementIndexSlot).toInt(),
    value = vstack.getFrameSlot(instruction.valueSlot),
)

internal inline fun TableSizeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableSizeS,
) {
    vstack.setFrameSlot(instruction.destinationSlot, instruction.table.elements.size.toLong())
}

private fun executeTableCopy(
    srcTable: TableInstance,
    destTable: TableInstance,
    elementsToCopy: Int,
    srcOffset: Int,
    dstOffset: Int,
) {
    try {
        srcTable.elements.copyInto(
            destination = destTable.elements,
            destinationOffset = dstOffset,
            startIndex = srcOffset,
            endIndex = srcOffset + elementsToCopy,
        )
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.TableOperationOutOfBounds)
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.TableOperationOutOfBounds)
    }
}

private fun executeTableFill(
    table: TableInstance,
    elementsToFill: Int,
    fillValue: Long,
    tableOffset: Int,
) {
    try {
        table.elements.fill(fillValue, tableOffset, tableOffset + elementsToFill)
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.TableOperationOutOfBounds)
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.TableOperationOutOfBounds)
    }
}

private fun executeTableGrow(
    vstack: ValueStack,
    table: TableInstance,
    elementsToAdd: Int,
    referenceValue: Long,
    destinationSlot: Int,
    max: Int,
) {
    val tableSize = table.elements.size
    val proposedLength = tableSize + elementsToAdd

    if (proposedLength < tableSize || proposedLength > max) {
        vstack.setFrameSlot(destinationSlot, -1L)
        return
    }

    table.type.limits.min = proposedLength.toULong()
    table.elements += LongArray(elementsToAdd) { referenceValue }
    vstack.setFrameSlot(destinationSlot, tableSize.toLong())
}

private fun executeTableInit(
    table: TableInstance,
    element: ElementInstance,
    elementsToInitialise: Int,
    segmentOffset: Int,
    tableOffset: Int,
) {
    try {
        element.elements.copyInto(table.elements, tableOffset, segmentOffset, segmentOffset + elementsToInitialise)
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.TableOperationOutOfBounds)
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.TableOperationOutOfBounds)
    }
}

private inline fun executeTableGet(
    vstack: ValueStack,
    table: TableInstance,
    elementIndex: Int,
    destinationSlot: Int,
) {
    vstack.setFrameSlot(destinationSlot, table.element(elementIndex))
}

private inline fun executeTableSet(
    table: TableInstance,
    elementIndex: Int,
    value: Long,
) {
    try {
        table.elements[elementIndex] = value
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.TableOperationOutOfBounds)
    }
}
