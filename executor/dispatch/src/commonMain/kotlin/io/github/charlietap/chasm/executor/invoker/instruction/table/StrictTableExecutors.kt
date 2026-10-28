package io.github.charlietap.chasm.executor.invoker.instruction.table

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.element
import io.github.charlietap.chasm.runtime.instance.ElementInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal fun executeTableCopy(
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

internal fun executeTableFill(
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

internal fun executeTableGrow(
    vstack: ValueStack,
    context: ExecutionContext,
    table: TableInstance,
    elementsToAdd: Int,
    referenceValue: Long,
    destinationSlot: Int,
) {
    val previousSize = table.grow(elementsToAdd, referenceValue)
    if (previousSize < 0 && context.store.config.limits.trapOnGrowFailure) {
        throw InvocationException(InvocationError.TableGrowFailed)
    }
    vstack.setFrameSlot(destinationSlot, previousSize.toLong())
}

internal fun executeTableInit(
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

internal inline fun executeTableGet(
    vstack: ValueStack,
    table: TableInstance,
    elementIndex: Int,
    destinationSlot: Int,
) {
    vstack.setFrameSlot(destinationSlot, table.element(elementIndex))
}

internal inline fun executeTableSet(
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
