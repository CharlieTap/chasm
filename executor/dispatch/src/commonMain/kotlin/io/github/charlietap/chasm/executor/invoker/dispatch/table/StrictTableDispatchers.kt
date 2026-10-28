package io.github.charlietap.chasm.executor.invoker.dispatch.table

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.table.executeTableCopy
import io.github.charlietap.chasm.executor.invoker.instruction.table.executeTableFill
import io.github.charlietap.chasm.executor.invoker.instruction.table.executeTableGet
import io.github.charlietap.chasm.executor.invoker.instruction.table.executeTableGrow
import io.github.charlietap.chasm.executor.invoker.instruction.table.executeTableInit
import io.github.charlietap.chasm.executor.invoker.instruction.table.executeTableSet
import io.github.charlietap.chasm.runtime.ext.element
import io.github.charlietap.chasm.runtime.instance.ElementInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance

fun TableCopyIiiDispatcher(
    elementsToCopy: Int,
    srcOffset: Int,
    dstOffset: Int,
    srcTable: TableInstance,
    destTable: TableInstance,
) = dispatchInstruction { vstack, context ->
    executeTableCopy(
        srcTable = srcTable,
        destTable = destTable,
        elementsToCopy = elementsToCopy,
        srcOffset = srcOffset,
        dstOffset = dstOffset,
    )
}

fun TableCopyIisDispatcher(
    elementsToCopy: Int,
    srcOffset: Int,
    dstOffsetSlot: Int,
    srcTable: TableInstance,
    destTable: TableInstance,
) = dispatchInstruction { vstack, context ->
    executeTableCopy(
        srcTable = srcTable,
        destTable = destTable,
        elementsToCopy = elementsToCopy,
        srcOffset = srcOffset,
        dstOffset = vstack.getFrameSlot(dstOffsetSlot).toInt(),
    )
}

fun TableCopyIsiDispatcher(
    elementsToCopy: Int,
    srcOffsetSlot: Int,
    dstOffset: Int,
    srcTable: TableInstance,
    destTable: TableInstance,
) = dispatchInstruction { vstack, context ->
    executeTableCopy(
        srcTable = srcTable,
        destTable = destTable,
        elementsToCopy = elementsToCopy,
        srcOffset = vstack.getFrameSlot(srcOffsetSlot).toInt(),
        dstOffset = dstOffset,
    )
}

fun TableCopyIssDispatcher(
    elementsToCopy: Int,
    srcOffsetSlot: Int,
    dstOffsetSlot: Int,
    srcTable: TableInstance,
    destTable: TableInstance,
) = dispatchInstruction { vstack, context ->
    executeTableCopy(
        srcTable = srcTable,
        destTable = destTable,
        elementsToCopy = elementsToCopy,
        srcOffset = vstack.getFrameSlot(srcOffsetSlot).toInt(),
        dstOffset = vstack.getFrameSlot(dstOffsetSlot).toInt(),
    )
}

fun TableCopySiiDispatcher(
    elementsToCopySlot: Int,
    srcOffset: Int,
    dstOffset: Int,
    srcTable: TableInstance,
    destTable: TableInstance,
) = dispatchInstruction { vstack, context ->
    executeTableCopy(
        srcTable = srcTable,
        destTable = destTable,
        elementsToCopy = vstack.getFrameSlot(elementsToCopySlot).toInt(),
        srcOffset = srcOffset,
        dstOffset = dstOffset,
    )
}

fun TableCopySisDispatcher(
    elementsToCopySlot: Int,
    srcOffset: Int,
    dstOffsetSlot: Int,
    srcTable: TableInstance,
    destTable: TableInstance,
) = dispatchInstruction { vstack, context ->
    executeTableCopy(
        srcTable = srcTable,
        destTable = destTable,
        elementsToCopy = vstack.getFrameSlot(elementsToCopySlot).toInt(),
        srcOffset = srcOffset,
        dstOffset = vstack.getFrameSlot(dstOffsetSlot).toInt(),
    )
}

fun TableCopySsiDispatcher(
    elementsToCopySlot: Int,
    srcOffsetSlot: Int,
    dstOffset: Int,
    srcTable: TableInstance,
    destTable: TableInstance,
) = dispatchInstruction { vstack, context ->
    executeTableCopy(
        srcTable = srcTable,
        destTable = destTable,
        elementsToCopy = vstack.getFrameSlot(elementsToCopySlot).toInt(),
        srcOffset = vstack.getFrameSlot(srcOffsetSlot).toInt(),
        dstOffset = dstOffset,
    )
}

fun TableCopySssDispatcher(
    elementsToCopySlot: Int,
    srcOffsetSlot: Int,
    dstOffsetSlot: Int,
    srcTable: TableInstance,
    destTable: TableInstance,
) = dispatchInstruction { vstack, context ->
    executeTableCopy(
        srcTable = srcTable,
        destTable = destTable,
        elementsToCopy = vstack.getFrameSlot(elementsToCopySlot).toInt(),
        srcOffset = vstack.getFrameSlot(srcOffsetSlot).toInt(),
        dstOffset = vstack.getFrameSlot(dstOffsetSlot).toInt(),
    )
}

fun TableFillIsiDispatcher(
    elementsToFill: Int,
    fillValueSlot: Int,
    tableOffset: Int,
    table: TableInstance,
) = dispatchInstruction { vstack, context ->
    executeTableFill(
        table = table,
        elementsToFill = elementsToFill,
        fillValue = vstack.getFrameSlot(fillValueSlot),
        tableOffset = tableOffset,
    )
}

fun TableFillIssDispatcher(
    elementsToFill: Int,
    fillValueSlot: Int,
    tableOffsetSlot: Int,
    table: TableInstance,
) = dispatchInstruction { vstack, context ->
    executeTableFill(
        table = table,
        elementsToFill = elementsToFill,
        fillValue = vstack.getFrameSlot(fillValueSlot),
        tableOffset = vstack.getFrameSlot(tableOffsetSlot).toInt(),
    )
}

fun TableFillSsiDispatcher(
    elementsToFillSlot: Int,
    fillValueSlot: Int,
    tableOffset: Int,
    table: TableInstance,
) = dispatchInstruction { vstack, context ->
    executeTableFill(
        table = table,
        elementsToFill = vstack.getFrameSlot(elementsToFillSlot).toInt(),
        fillValue = vstack.getFrameSlot(fillValueSlot),
        tableOffset = tableOffset,
    )
}

fun TableFillSssDispatcher(
    elementsToFillSlot: Int,
    fillValueSlot: Int,
    tableOffsetSlot: Int,
    table: TableInstance,
) = dispatchInstruction { vstack, context ->
    executeTableFill(
        table = table,
        elementsToFill = vstack.getFrameSlot(elementsToFillSlot).toInt(),
        fillValue = vstack.getFrameSlot(fillValueSlot),
        tableOffset = vstack.getFrameSlot(tableOffsetSlot).toInt(),
    )
}

fun TableGrowIsDispatcher(
    elementsToAdd: Int,
    referenceValueSlot: Int,
    destinationSlot: Int,
    table: TableInstance,
) = dispatchInstruction { vstack, context ->
    executeTableGrow(
        vstack = vstack,
        context = context,
        table = table,
        elementsToAdd = elementsToAdd,
        referenceValue = vstack.getFrameSlot(referenceValueSlot),
        destinationSlot = destinationSlot,
    )
}

fun TableGrowSsDispatcher(
    elementsToAddSlot: Int,
    referenceValueSlot: Int,
    destinationSlot: Int,
    table: TableInstance,
) = dispatchInstruction { vstack, context ->
    executeTableGrow(
        vstack = vstack,
        context = context,
        table = table,
        elementsToAdd = vstack.getFrameSlot(elementsToAddSlot).toInt(),
        referenceValue = vstack.getFrameSlot(referenceValueSlot),
        destinationSlot = destinationSlot,
    )
}

fun TableInitIiiDispatcher(
    elementsToInitialise: Int,
    segmentOffset: Int,
    tableOffset: Int,
    element: ElementInstance,
    table: TableInstance,
) = dispatchInstruction { vstack, context ->
    executeTableInit(
        table = table,
        element = element,
        elementsToInitialise = elementsToInitialise,
        segmentOffset = segmentOffset,
        tableOffset = tableOffset,
    )
}

fun TableInitIisDispatcher(
    elementsToInitialise: Int,
    segmentOffset: Int,
    tableOffsetSlot: Int,
    element: ElementInstance,
    table: TableInstance,
) = dispatchInstruction { vstack, context ->
    executeTableInit(
        table = table,
        element = element,
        elementsToInitialise = elementsToInitialise,
        segmentOffset = segmentOffset,
        tableOffset = vstack.getFrameSlot(tableOffsetSlot).toInt(),
    )
}

fun TableInitIsiDispatcher(
    elementsToInitialise: Int,
    segmentOffsetSlot: Int,
    tableOffset: Int,
    element: ElementInstance,
    table: TableInstance,
) = dispatchInstruction { vstack, context ->
    executeTableInit(
        table = table,
        element = element,
        elementsToInitialise = elementsToInitialise,
        segmentOffset = vstack.getFrameSlot(segmentOffsetSlot).toInt(),
        tableOffset = tableOffset,
    )
}

fun TableInitIssDispatcher(
    elementsToInitialise: Int,
    segmentOffsetSlot: Int,
    tableOffsetSlot: Int,
    element: ElementInstance,
    table: TableInstance,
) = dispatchInstruction { vstack, context ->
    executeTableInit(
        table = table,
        element = element,
        elementsToInitialise = elementsToInitialise,
        segmentOffset = vstack.getFrameSlot(segmentOffsetSlot).toInt(),
        tableOffset = vstack.getFrameSlot(tableOffsetSlot).toInt(),
    )
}

fun TableInitSiiDispatcher(
    elementsToInitialiseSlot: Int,
    segmentOffset: Int,
    tableOffset: Int,
    element: ElementInstance,
    table: TableInstance,
) = dispatchInstruction { vstack, context ->
    executeTableInit(
        table = table,
        element = element,
        elementsToInitialise = vstack.getFrameSlot(elementsToInitialiseSlot).toInt(),
        segmentOffset = segmentOffset,
        tableOffset = tableOffset,
    )
}

fun TableInitSisDispatcher(
    elementsToInitialiseSlot: Int,
    segmentOffset: Int,
    tableOffsetSlot: Int,
    element: ElementInstance,
    table: TableInstance,
) = dispatchInstruction { vstack, context ->
    executeTableInit(
        table = table,
        element = element,
        elementsToInitialise = vstack.getFrameSlot(elementsToInitialiseSlot).toInt(),
        segmentOffset = segmentOffset,
        tableOffset = vstack.getFrameSlot(tableOffsetSlot).toInt(),
    )
}

fun TableInitSsiDispatcher(
    elementsToInitialiseSlot: Int,
    segmentOffsetSlot: Int,
    tableOffset: Int,
    element: ElementInstance,
    table: TableInstance,
) = dispatchInstruction { vstack, context ->
    executeTableInit(
        table = table,
        element = element,
        elementsToInitialise = vstack.getFrameSlot(elementsToInitialiseSlot).toInt(),
        segmentOffset = vstack.getFrameSlot(segmentOffsetSlot).toInt(),
        tableOffset = tableOffset,
    )
}

fun TableInitSssDispatcher(
    elementsToInitialiseSlot: Int,
    segmentOffsetSlot: Int,
    tableOffsetSlot: Int,
    element: ElementInstance,
    table: TableInstance,
) = dispatchInstruction { vstack, context ->
    executeTableInit(
        table = table,
        element = element,
        elementsToInitialise = vstack.getFrameSlot(elementsToInitialiseSlot).toInt(),
        segmentOffset = vstack.getFrameSlot(segmentOffsetSlot).toInt(),
        tableOffset = vstack.getFrameSlot(tableOffsetSlot).toInt(),
    )
}

fun TableGetIDispatcher(
    elementIndex: Int,
    destinationSlot: Int,
    table: TableInstance,
) = dispatchInstruction { vstack, context ->
    executeTableGet(
        vstack = vstack,
        table = table,
        elementIndex = elementIndex,
        destinationSlot = destinationSlot,
    )
}

fun TableGetSDispatcher(
    elementIndexSlot: Int,
    destinationSlot: Int,
    table: TableInstance,
) = dispatchInstruction { vstack, context ->
    executeTableGet(
        vstack = vstack,
        table = table,
        elementIndex = vstack.getFrameSlot(elementIndexSlot).toInt(),
        destinationSlot = destinationSlot,
    )
}

fun TableSetSiDispatcher(
    valueSlot: Int,
    elementIndex: Int,
    table: TableInstance,
) = dispatchInstruction { vstack, context ->
    executeTableSet(
        table = table,
        elementIndex = elementIndex,
        value = vstack.getFrameSlot(valueSlot),
    )
}

fun TableSetSsDispatcher(
    valueSlot: Int,
    elementIndexSlot: Int,
    table: TableInstance,
) = dispatchInstruction { vstack, context ->
    executeTableSet(
        table = table,
        elementIndex = vstack.getFrameSlot(elementIndexSlot).toInt(),
        value = vstack.getFrameSlot(valueSlot),
    )
}

fun TableSizeSDispatcher(
    destinationSlot: Int,
    table: TableInstance,
) = dispatchInstruction { vstack, context ->
    run {
        vstack.setFrameSlot(destinationSlot, table.elements.size.toLong())
    }
}
