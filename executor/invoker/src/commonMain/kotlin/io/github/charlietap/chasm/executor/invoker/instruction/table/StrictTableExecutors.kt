package io.github.charlietap.chasm.executor.invoker.instruction.table

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.element
import io.github.charlietap.chasm.runtime.instance.ElementInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.instruction.TableInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal fun TableCopyExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: TableInstruction.TableCopyIii,
) = executeTableCopy(
    srcTable = instruction.srcTable,
    destTable = instruction.destTable,
    elementsToCopy = instruction.elementsToCopy,
    srcOffset = instruction.srcOffset,
    dstOffset = instruction.dstOffset,
)

internal fun TableCopyExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: TableInstruction.TableCopyIis,
) = executeTableCopy(
    srcTable = instruction.srcTable,
    destTable = instruction.destTable,
    elementsToCopy = instruction.elementsToCopy,
    srcOffset = instruction.srcOffset,
    dstOffset = vstack.getFrameSlot(instruction.dstOffsetSlot).toInt(),
)

internal fun TableCopyExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: TableInstruction.TableCopyIsi,
) = executeTableCopy(
    srcTable = instruction.srcTable,
    destTable = instruction.destTable,
    elementsToCopy = instruction.elementsToCopy,
    srcOffset = vstack.getFrameSlot(instruction.srcOffsetSlot).toInt(),
    dstOffset = instruction.dstOffset,
)

internal fun TableCopyExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: TableInstruction.TableCopyIss,
) = executeTableCopy(
    srcTable = instruction.srcTable,
    destTable = instruction.destTable,
    elementsToCopy = instruction.elementsToCopy,
    srcOffset = vstack.getFrameSlot(instruction.srcOffsetSlot).toInt(),
    dstOffset = vstack.getFrameSlot(instruction.dstOffsetSlot).toInt(),
)

internal fun TableCopyExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: TableInstruction.TableCopySii,
) = executeTableCopy(
    srcTable = instruction.srcTable,
    destTable = instruction.destTable,
    elementsToCopy = vstack.getFrameSlot(instruction.elementsToCopySlot).toInt(),
    srcOffset = instruction.srcOffset,
    dstOffset = instruction.dstOffset,
)

internal fun TableCopyExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: TableInstruction.TableCopySis,
) = executeTableCopy(
    srcTable = instruction.srcTable,
    destTable = instruction.destTable,
    elementsToCopy = vstack.getFrameSlot(instruction.elementsToCopySlot).toInt(),
    srcOffset = instruction.srcOffset,
    dstOffset = vstack.getFrameSlot(instruction.dstOffsetSlot).toInt(),
)

internal fun TableCopyExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: TableInstruction.TableCopySsi,
) = executeTableCopy(
    srcTable = instruction.srcTable,
    destTable = instruction.destTable,
    elementsToCopy = vstack.getFrameSlot(instruction.elementsToCopySlot).toInt(),
    srcOffset = vstack.getFrameSlot(instruction.srcOffsetSlot).toInt(),
    dstOffset = instruction.dstOffset,
)

internal fun TableCopyExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: TableInstruction.TableCopySss,
) = executeTableCopy(
    srcTable = instruction.srcTable,
    destTable = instruction.destTable,
    elementsToCopy = vstack.getFrameSlot(instruction.elementsToCopySlot).toInt(),
    srcOffset = vstack.getFrameSlot(instruction.srcOffsetSlot).toInt(),
    dstOffset = vstack.getFrameSlot(instruction.dstOffsetSlot).toInt(),
)

internal fun TableFillExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: TableInstruction.TableFillIsi,
) = executeTableFill(
    table = instruction.table,
    elementsToFill = instruction.elementsToFill,
    fillValue = vstack.getFrameSlot(instruction.fillValueSlot),
    tableOffset = instruction.tableOffset,
)

internal fun TableFillExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: TableInstruction.TableFillIss,
) = executeTableFill(
    table = instruction.table,
    elementsToFill = instruction.elementsToFill,
    fillValue = vstack.getFrameSlot(instruction.fillValueSlot),
    tableOffset = vstack.getFrameSlot(instruction.tableOffsetSlot).toInt(),
)

internal fun TableFillExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: TableInstruction.TableFillSsi,
) = executeTableFill(
    table = instruction.table,
    elementsToFill = vstack.getFrameSlot(instruction.elementsToFillSlot).toInt(),
    fillValue = vstack.getFrameSlot(instruction.fillValueSlot),
    tableOffset = instruction.tableOffset,
)

internal fun TableFillExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: TableInstruction.TableFillSss,
) = executeTableFill(
    table = instruction.table,
    elementsToFill = vstack.getFrameSlot(instruction.elementsToFillSlot).toInt(),
    fillValue = vstack.getFrameSlot(instruction.fillValueSlot),
    tableOffset = vstack.getFrameSlot(instruction.tableOffsetSlot).toInt(),
)

internal fun TableGrowExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: TableInstruction.TableGrowIs,
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
    context: ExecutionContext,
    instruction: TableInstruction.TableGrowSs,
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
    context: ExecutionContext,
    instruction: TableInstruction.TableInitIii,
) = executeTableInit(
    table = instruction.table,
    element = instruction.element,
    elementsToInitialise = instruction.elementsToInitialise,
    segmentOffset = instruction.segmentOffset,
    tableOffset = instruction.tableOffset,
)

internal fun TableInitExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: TableInstruction.TableInitIis,
) = executeTableInit(
    table = instruction.table,
    element = instruction.element,
    elementsToInitialise = instruction.elementsToInitialise,
    segmentOffset = instruction.segmentOffset,
    tableOffset = vstack.getFrameSlot(instruction.tableOffsetSlot).toInt(),
)

internal fun TableInitExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: TableInstruction.TableInitIsi,
) = executeTableInit(
    table = instruction.table,
    element = instruction.element,
    elementsToInitialise = instruction.elementsToInitialise,
    segmentOffset = vstack.getFrameSlot(instruction.segmentOffsetSlot).toInt(),
    tableOffset = instruction.tableOffset,
)

internal fun TableInitExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: TableInstruction.TableInitIss,
) = executeTableInit(
    table = instruction.table,
    element = instruction.element,
    elementsToInitialise = instruction.elementsToInitialise,
    segmentOffset = vstack.getFrameSlot(instruction.segmentOffsetSlot).toInt(),
    tableOffset = vstack.getFrameSlot(instruction.tableOffsetSlot).toInt(),
)

internal fun TableInitExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: TableInstruction.TableInitSii,
) = executeTableInit(
    table = instruction.table,
    element = instruction.element,
    elementsToInitialise = vstack.getFrameSlot(instruction.elementsToInitialiseSlot).toInt(),
    segmentOffset = instruction.segmentOffset,
    tableOffset = instruction.tableOffset,
)

internal fun TableInitExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: TableInstruction.TableInitSis,
) = executeTableInit(
    table = instruction.table,
    element = instruction.element,
    elementsToInitialise = vstack.getFrameSlot(instruction.elementsToInitialiseSlot).toInt(),
    segmentOffset = instruction.segmentOffset,
    tableOffset = vstack.getFrameSlot(instruction.tableOffsetSlot).toInt(),
)

internal fun TableInitExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: TableInstruction.TableInitSsi,
) = executeTableInit(
    table = instruction.table,
    element = instruction.element,
    elementsToInitialise = vstack.getFrameSlot(instruction.elementsToInitialiseSlot).toInt(),
    segmentOffset = vstack.getFrameSlot(instruction.segmentOffsetSlot).toInt(),
    tableOffset = instruction.tableOffset,
)

internal fun TableInitExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: TableInstruction.TableInitSss,
) = executeTableInit(
    table = instruction.table,
    element = instruction.element,
    elementsToInitialise = vstack.getFrameSlot(instruction.elementsToInitialiseSlot).toInt(),
    segmentOffset = vstack.getFrameSlot(instruction.segmentOffsetSlot).toInt(),
    tableOffset = vstack.getFrameSlot(instruction.tableOffsetSlot).toInt(),
)

internal inline fun TableGetExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: TableInstruction.TableGetI,
) = executeTableGet(
    vstack = vstack,
    table = instruction.table,
    elementIndex = instruction.elementIndex,
    destinationSlot = instruction.destinationSlot,
)

internal inline fun TableGetExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: TableInstruction.TableGetS,
) = executeTableGet(
    vstack = vstack,
    table = instruction.table,
    elementIndex = vstack.getFrameSlot(instruction.elementIndexSlot).toInt(),
    destinationSlot = instruction.destinationSlot,
)

internal inline fun TableSetExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: TableInstruction.TableSetSi,
) = executeTableSet(
    table = instruction.table,
    elementIndex = instruction.elementIndex,
    value = vstack.getFrameSlot(instruction.valueSlot),
)

internal inline fun TableSetExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: TableInstruction.TableSetSs,
) = executeTableSet(
    table = instruction.table,
    elementIndex = vstack.getFrameSlot(instruction.elementIndexSlot).toInt(),
    value = vstack.getFrameSlot(instruction.valueSlot),
)

internal inline fun TableSizeExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: TableInstruction.TableSizeS,
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
