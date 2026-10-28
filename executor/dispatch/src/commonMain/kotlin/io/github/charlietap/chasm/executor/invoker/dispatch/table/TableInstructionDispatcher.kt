package io.github.charlietap.chasm.executor.invoker.dispatch.table

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.TableInstruction

fun TableInstructionDispatcher(
    instruction: TableInstruction,
): DispatchableInstruction = when (instruction) {
    is TableInstruction.TableCopyIii -> TableCopyIiiDispatcher(instruction.elementsToCopy, instruction.srcOffset, instruction.dstOffset, instruction.srcTable, instruction.destTable)
    is TableInstruction.TableCopyIis -> TableCopyIisDispatcher(instruction.elementsToCopy, instruction.srcOffset, instruction.dstOffsetSlot, instruction.srcTable, instruction.destTable)
    is TableInstruction.TableCopyIsi -> TableCopyIsiDispatcher(instruction.elementsToCopy, instruction.srcOffsetSlot, instruction.dstOffset, instruction.srcTable, instruction.destTable)
    is TableInstruction.TableCopyIss -> TableCopyIssDispatcher(instruction.elementsToCopy, instruction.srcOffsetSlot, instruction.dstOffsetSlot, instruction.srcTable, instruction.destTable)
    is TableInstruction.TableCopySii -> TableCopySiiDispatcher(instruction.elementsToCopySlot, instruction.srcOffset, instruction.dstOffset, instruction.srcTable, instruction.destTable)
    is TableInstruction.TableCopySis -> TableCopySisDispatcher(instruction.elementsToCopySlot, instruction.srcOffset, instruction.dstOffsetSlot, instruction.srcTable, instruction.destTable)
    is TableInstruction.TableCopySsi -> TableCopySsiDispatcher(instruction.elementsToCopySlot, instruction.srcOffsetSlot, instruction.dstOffset, instruction.srcTable, instruction.destTable)
    is TableInstruction.TableCopySss -> TableCopySssDispatcher(instruction.elementsToCopySlot, instruction.srcOffsetSlot, instruction.dstOffsetSlot, instruction.srcTable, instruction.destTable)
    is TableInstruction.TableFillIsi -> TableFillIsiDispatcher(instruction.elementsToFill, instruction.fillValueSlot, instruction.tableOffset, instruction.table)
    is TableInstruction.TableFillIss -> TableFillIssDispatcher(instruction.elementsToFill, instruction.fillValueSlot, instruction.tableOffsetSlot, instruction.table)
    is TableInstruction.TableFillSsi -> TableFillSsiDispatcher(instruction.elementsToFillSlot, instruction.fillValueSlot, instruction.tableOffset, instruction.table)
    is TableInstruction.TableFillSss -> TableFillSssDispatcher(instruction.elementsToFillSlot, instruction.fillValueSlot, instruction.tableOffsetSlot, instruction.table)
    is TableInstruction.TableGrowIs -> TableGrowIsDispatcher(instruction.elementsToAdd, instruction.referenceValueSlot, instruction.destinationSlot, instruction.table)
    is TableInstruction.TableGrowSs -> TableGrowSsDispatcher(instruction.elementsToAddSlot, instruction.referenceValueSlot, instruction.destinationSlot, instruction.table)
    is TableInstruction.TableInitIii -> TableInitIiiDispatcher(instruction.elementsToInitialise, instruction.segmentOffset, instruction.tableOffset, instruction.element, instruction.table)
    is TableInstruction.TableInitIis -> TableInitIisDispatcher(instruction.elementsToInitialise, instruction.segmentOffset, instruction.tableOffsetSlot, instruction.element, instruction.table)
    is TableInstruction.TableInitIsi -> TableInitIsiDispatcher(instruction.elementsToInitialise, instruction.segmentOffsetSlot, instruction.tableOffset, instruction.element, instruction.table)
    is TableInstruction.TableInitIss -> TableInitIssDispatcher(instruction.elementsToInitialise, instruction.segmentOffsetSlot, instruction.tableOffsetSlot, instruction.element, instruction.table)
    is TableInstruction.TableInitSii -> TableInitSiiDispatcher(instruction.elementsToInitialiseSlot, instruction.segmentOffset, instruction.tableOffset, instruction.element, instruction.table)
    is TableInstruction.TableInitSis -> TableInitSisDispatcher(instruction.elementsToInitialiseSlot, instruction.segmentOffset, instruction.tableOffsetSlot, instruction.element, instruction.table)
    is TableInstruction.TableInitSsi -> TableInitSsiDispatcher(instruction.elementsToInitialiseSlot, instruction.segmentOffsetSlot, instruction.tableOffset, instruction.element, instruction.table)
    is TableInstruction.TableInitSss -> TableInitSssDispatcher(instruction.elementsToInitialiseSlot, instruction.segmentOffsetSlot, instruction.tableOffsetSlot, instruction.element, instruction.table)
    is TableInstruction.TableGetI -> TableGetIDispatcher(instruction.elementIndex, instruction.destinationSlot, instruction.table)
    is TableInstruction.TableGetS -> TableGetSDispatcher(instruction.elementIndexSlot, instruction.destinationSlot, instruction.table)
    is TableInstruction.TableSetSi -> TableSetSiDispatcher(instruction.valueSlot, instruction.elementIndex, instruction.table)
    is TableInstruction.TableSetSs -> TableSetSsDispatcher(instruction.valueSlot, instruction.elementIndexSlot, instruction.table)
    is TableInstruction.TableSizeS -> TableSizeSDispatcher(instruction.destinationSlot, instruction.table)
    is TableInstruction.ElemDrop -> ElemDropDispatcher(instruction.element)
}
