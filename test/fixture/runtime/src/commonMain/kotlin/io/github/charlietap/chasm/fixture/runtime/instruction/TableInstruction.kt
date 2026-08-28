package io.github.charlietap.chasm.fixture.runtime.instruction

import io.github.charlietap.chasm.fixture.runtime.instance.elementInstance
import io.github.charlietap.chasm.fixture.runtime.instance.tableInstance
import io.github.charlietap.chasm.runtime.instance.ElementInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.instruction.TableInstruction

fun tableRuntimeInstruction(): TableInstruction = tableGetIRuntimeInstruction()

fun tableGetIRuntimeInstruction(
    elementIndex: Int = 0,
    destinationSlot: Int = 0,
    table: TableInstance = tableInstance(),
) = TableInstruction.TableGetI(
    elementIndex = elementIndex,
    destinationSlot = destinationSlot,
    table = table,
)

fun tableGetSRuntimeInstruction(
    elementIndexSlot: Int = 0,
    destinationSlot: Int = 0,
    table: TableInstance = tableInstance(),
) = TableInstruction.TableGetS(
    elementIndexSlot = elementIndexSlot,
    destinationSlot = destinationSlot,
    table = table,
)

fun tableSetSiRuntimeInstruction(
    valueSlot: Int = 0,
    elementIndex: Int = 0,
    table: TableInstance = tableInstance(),
) = TableInstruction.TableSetSi(
    valueSlot = valueSlot,
    elementIndex = elementIndex,
    table = table,
)

fun tableSetSsRuntimeInstruction(
    valueSlot: Int = 0,
    elementIndexSlot: Int = 0,
    table: TableInstance = tableInstance(),
) = TableInstruction.TableSetSs(
    valueSlot = valueSlot,
    elementIndexSlot = elementIndexSlot,
    table = table,
)

fun tableInitIiiRuntimeInstruction(
    elementsToInitialise: Int = 0,
    segmentOffset: Int = 0,
    tableOffset: Int = 0,
    element: ElementInstance = elementInstance(),
    table: TableInstance = tableInstance(),
) = TableInstruction.TableInitIii(
    elementsToInitialise = elementsToInitialise,
    segmentOffset = segmentOffset,
    tableOffset = tableOffset,
    element = element,
    table = table,
)

fun tableInitIisRuntimeInstruction(
    elementsToInitialise: Int = 0,
    segmentOffset: Int = 0,
    tableOffsetSlot: Int = 0,
    element: ElementInstance = elementInstance(),
    table: TableInstance = tableInstance(),
) = TableInstruction.TableInitIis(
    elementsToInitialise = elementsToInitialise,
    segmentOffset = segmentOffset,
    tableOffsetSlot = tableOffsetSlot,
    element = element,
    table = table,
)

fun tableInitIsiRuntimeInstruction(
    elementsToInitialise: Int = 0,
    segmentOffsetSlot: Int = 0,
    tableOffset: Int = 0,
    element: ElementInstance = elementInstance(),
    table: TableInstance = tableInstance(),
) = TableInstruction.TableInitIsi(
    elementsToInitialise = elementsToInitialise,
    segmentOffsetSlot = segmentOffsetSlot,
    tableOffset = tableOffset,
    element = element,
    table = table,
)

fun tableInitIssRuntimeInstruction(
    elementsToInitialise: Int = 0,
    segmentOffsetSlot: Int = 0,
    tableOffsetSlot: Int = 0,
    element: ElementInstance = elementInstance(),
    table: TableInstance = tableInstance(),
) = TableInstruction.TableInitIss(
    elementsToInitialise = elementsToInitialise,
    segmentOffsetSlot = segmentOffsetSlot,
    tableOffsetSlot = tableOffsetSlot,
    element = element,
    table = table,
)

fun tableInitSiiRuntimeInstruction(
    elementsToInitialiseSlot: Int = 0,
    segmentOffset: Int = 0,
    tableOffset: Int = 0,
    element: ElementInstance = elementInstance(),
    table: TableInstance = tableInstance(),
) = TableInstruction.TableInitSii(
    elementsToInitialiseSlot = elementsToInitialiseSlot,
    segmentOffset = segmentOffset,
    tableOffset = tableOffset,
    element = element,
    table = table,
)

fun tableInitSisRuntimeInstruction(
    elementsToInitialiseSlot: Int = 0,
    segmentOffset: Int = 0,
    tableOffsetSlot: Int = 0,
    element: ElementInstance = elementInstance(),
    table: TableInstance = tableInstance(),
) = TableInstruction.TableInitSis(
    elementsToInitialiseSlot = elementsToInitialiseSlot,
    segmentOffset = segmentOffset,
    tableOffsetSlot = tableOffsetSlot,
    element = element,
    table = table,
)

fun tableInitSsiRuntimeInstruction(
    elementsToInitialiseSlot: Int = 0,
    segmentOffsetSlot: Int = 0,
    tableOffset: Int = 0,
    element: ElementInstance = elementInstance(),
    table: TableInstance = tableInstance(),
) = TableInstruction.TableInitSsi(
    elementsToInitialiseSlot = elementsToInitialiseSlot,
    segmentOffsetSlot = segmentOffsetSlot,
    tableOffset = tableOffset,
    element = element,
    table = table,
)

fun tableInitSssRuntimeInstruction(
    elementsToInitialiseSlot: Int = 0,
    segmentOffsetSlot: Int = 0,
    tableOffsetSlot: Int = 0,
    element: ElementInstance = elementInstance(),
    table: TableInstance = tableInstance(),
) = TableInstruction.TableInitSss(
    elementsToInitialiseSlot = elementsToInitialiseSlot,
    segmentOffsetSlot = segmentOffsetSlot,
    tableOffsetSlot = tableOffsetSlot,
    element = element,
    table = table,
)

fun tableCopyIiiRuntimeInstruction(
    elementsToCopy: Int = 0,
    srcOffset: Int = 0,
    dstOffset: Int = 0,
    srcTable: TableInstance = tableInstance(),
    destTable: TableInstance = tableInstance(),
) = TableInstruction.TableCopyIii(
    elementsToCopy = elementsToCopy,
    srcOffset = srcOffset,
    dstOffset = dstOffset,
    srcTable = srcTable,
    destTable = destTable,
)

fun tableCopyIisRuntimeInstruction(
    elementsToCopy: Int = 0,
    srcOffset: Int = 0,
    dstOffsetSlot: Int = 0,
    srcTable: TableInstance = tableInstance(),
    destTable: TableInstance = tableInstance(),
) = TableInstruction.TableCopyIis(
    elementsToCopy = elementsToCopy,
    srcOffset = srcOffset,
    dstOffsetSlot = dstOffsetSlot,
    srcTable = srcTable,
    destTable = destTable,
)

fun tableCopyIsiRuntimeInstruction(
    elementsToCopy: Int = 0,
    srcOffsetSlot: Int = 0,
    dstOffset: Int = 0,
    srcTable: TableInstance = tableInstance(),
    destTable: TableInstance = tableInstance(),
) = TableInstruction.TableCopyIsi(
    elementsToCopy = elementsToCopy,
    srcOffsetSlot = srcOffsetSlot,
    dstOffset = dstOffset,
    srcTable = srcTable,
    destTable = destTable,
)

fun tableCopyIssRuntimeInstruction(
    elementsToCopy: Int = 0,
    srcOffsetSlot: Int = 0,
    dstOffsetSlot: Int = 0,
    srcTable: TableInstance = tableInstance(),
    destTable: TableInstance = tableInstance(),
) = TableInstruction.TableCopyIss(
    elementsToCopy = elementsToCopy,
    srcOffsetSlot = srcOffsetSlot,
    dstOffsetSlot = dstOffsetSlot,
    srcTable = srcTable,
    destTable = destTable,
)

fun tableCopySiiRuntimeInstruction(
    elementsToCopySlot: Int = 0,
    srcOffset: Int = 0,
    dstOffset: Int = 0,
    srcTable: TableInstance = tableInstance(),
    destTable: TableInstance = tableInstance(),
) = TableInstruction.TableCopySii(
    elementsToCopySlot = elementsToCopySlot,
    srcOffset = srcOffset,
    dstOffset = dstOffset,
    srcTable = srcTable,
    destTable = destTable,
)

fun tableCopySisRuntimeInstruction(
    elementsToCopySlot: Int = 0,
    srcOffset: Int = 0,
    dstOffsetSlot: Int = 0,
    srcTable: TableInstance = tableInstance(),
    destTable: TableInstance = tableInstance(),
) = TableInstruction.TableCopySis(
    elementsToCopySlot = elementsToCopySlot,
    srcOffset = srcOffset,
    dstOffsetSlot = dstOffsetSlot,
    srcTable = srcTable,
    destTable = destTable,
)

fun tableCopySsiRuntimeInstruction(
    elementsToCopySlot: Int = 0,
    srcOffsetSlot: Int = 0,
    dstOffset: Int = 0,
    srcTable: TableInstance = tableInstance(),
    destTable: TableInstance = tableInstance(),
) = TableInstruction.TableCopySsi(
    elementsToCopySlot = elementsToCopySlot,
    srcOffsetSlot = srcOffsetSlot,
    dstOffset = dstOffset,
    srcTable = srcTable,
    destTable = destTable,
)

fun tableCopySssRuntimeInstruction(
    elementsToCopySlot: Int = 0,
    srcOffsetSlot: Int = 0,
    dstOffsetSlot: Int = 0,
    srcTable: TableInstance = tableInstance(),
    destTable: TableInstance = tableInstance(),
) = TableInstruction.TableCopySss(
    elementsToCopySlot = elementsToCopySlot,
    srcOffsetSlot = srcOffsetSlot,
    dstOffsetSlot = dstOffsetSlot,
    srcTable = srcTable,
    destTable = destTable,
)

fun tableGrowIsRuntimeInstruction(
    elementsToAdd: Int = 0,
    referenceValueSlot: Int = 0,
    destinationSlot: Int = 0,
    table: TableInstance = tableInstance(),
    max: Int = 0,
) = TableInstruction.TableGrowIs(
    elementsToAdd = elementsToAdd,
    referenceValueSlot = referenceValueSlot,
    destinationSlot = destinationSlot,
    table = table,
    max = max,
)

fun tableGrowSsRuntimeInstruction(
    elementsToAddSlot: Int = 0,
    referenceValueSlot: Int = 0,
    destinationSlot: Int = 0,
    table: TableInstance = tableInstance(),
    max: Int = 0,
) = TableInstruction.TableGrowSs(
    elementsToAddSlot = elementsToAddSlot,
    referenceValueSlot = referenceValueSlot,
    destinationSlot = destinationSlot,
    table = table,
    max = max,
)

fun tableSizeSRuntimeInstruction(
    destinationSlot: Int = 0,
    table: TableInstance = tableInstance(),
) = TableInstruction.TableSizeS(
    destinationSlot = destinationSlot,
    table = table,
)

fun tableFillIsiRuntimeInstruction(
    elementsToFill: Int = 0,
    fillValueSlot: Int = 0,
    tableOffset: Int = 0,
    table: TableInstance = tableInstance(),
) = TableInstruction.TableFillIsi(
    elementsToFill = elementsToFill,
    fillValueSlot = fillValueSlot,
    tableOffset = tableOffset,
    table = table,
)

fun tableFillIssRuntimeInstruction(
    elementsToFill: Int = 0,
    fillValueSlot: Int = 0,
    tableOffsetSlot: Int = 0,
    table: TableInstance = tableInstance(),
) = TableInstruction.TableFillIss(
    elementsToFill = elementsToFill,
    fillValueSlot = fillValueSlot,
    tableOffsetSlot = tableOffsetSlot,
    table = table,
)

fun tableFillSsiRuntimeInstruction(
    elementsToFillSlot: Int = 0,
    fillValueSlot: Int = 0,
    tableOffset: Int = 0,
    table: TableInstance = tableInstance(),
) = TableInstruction.TableFillSsi(
    elementsToFillSlot = elementsToFillSlot,
    fillValueSlot = fillValueSlot,
    tableOffset = tableOffset,
    table = table,
)

fun tableFillSssRuntimeInstruction(
    elementsToFillSlot: Int = 0,
    fillValueSlot: Int = 0,
    tableOffsetSlot: Int = 0,
    table: TableInstance = tableInstance(),
) = TableInstruction.TableFillSss(
    elementsToFillSlot = elementsToFillSlot,
    fillValueSlot = fillValueSlot,
    tableOffsetSlot = tableOffsetSlot,
    table = table,
)

fun elemDropRuntimeInstruction(
    element: ElementInstance = elementInstance(),
) = TableInstruction.ElemDrop(
    element = element,
)
