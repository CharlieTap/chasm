package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.cir.TableInstruction
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_ELEMENT_DROP
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_TABLE_COPY
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_TABLE_FILL
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_TABLE_GET
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_TABLE_GROW
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_TABLE_INIT
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_TABLE_SET
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_TABLE_SIZE
import io.github.charlietap.chasm.compiler.backend.abi.REFERENCE_SEGMENT
import io.github.charlietap.chasm.compiler.backend.abi.REFERENCE_TABLE
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.compiler.backend.analysis.form.immediate
import io.github.charlietap.chasm.compiler.backend.analysis.form.slot

internal fun lowerManagedTableInstruction(
    instruction: TableInstruction,
    context: LowerContext,
) {
    when (val i = instruction) {
        is TableInstruction.TableGetI -> context.tableGet(i.table.index, immediate(i.elementIndex.toLong()), i.destinationSlot)
        is TableInstruction.TableGetS -> context.tableGet(i.table.index, slot(i.elementIndexSlot), i.destinationSlot)
        is TableInstruction.TableSetSi -> context.tableSet(i.table.index, immediate(i.elementIndex.toLong()), i.valueSlot)
        is TableInstruction.TableSetSs -> context.tableSet(i.table.index, slot(i.elementIndexSlot), i.valueSlot)
        is TableInstruction.TableSizeS -> context.tableSize(i.table.index, i.destinationSlot)
        is TableInstruction.TableGrowIs -> context.tableGrow(i.table.index, immediate(i.elementsToAdd.toLong()), i.referenceValueSlot, i.destinationSlot)
        is TableInstruction.TableGrowSs -> context.tableGrow(i.table.index, slot(i.elementsToAddSlot), i.referenceValueSlot, i.destinationSlot)
        is TableInstruction.TableFillIsi -> context.tableFill(i.table.index, immediate(i.tableOffset.toLong()), i.fillValueSlot, immediate(i.elementsToFill.toLong()))
        is TableInstruction.TableFillIss -> context.tableFill(i.table.index, slot(i.tableOffsetSlot), i.fillValueSlot, immediate(i.elementsToFill.toLong()))
        is TableInstruction.TableFillSsi -> context.tableFill(i.table.index, immediate(i.tableOffset.toLong()), i.fillValueSlot, slot(i.elementsToFillSlot))
        is TableInstruction.TableFillSss -> context.tableFill(i.table.index, slot(i.tableOffsetSlot), i.fillValueSlot, slot(i.elementsToFillSlot))
        is TableInstruction.TableCopyIii -> context.tableCopy(i.srcTable.index, i.destTable.index, immediate(i.srcOffset.toLong()), immediate(i.dstOffset.toLong()), immediate(i.elementsToCopy.toLong()))
        is TableInstruction.TableInitIii -> context.tableInit(i.table.index, i.element.index, immediate(i.segmentOffset.toLong()), immediate(i.tableOffset.toLong()), immediate(i.elementsToInitialise.toLong()))
        is TableInstruction.TableCopyIis -> context.tableCopy(i.srcTable.index, i.destTable.index, immediate(i.srcOffset.toLong()), slot(i.dstOffsetSlot), immediate(i.elementsToCopy.toLong()))
        is TableInstruction.TableInitIis -> context.tableInit(i.table.index, i.element.index, immediate(i.segmentOffset.toLong()), slot(i.tableOffsetSlot), immediate(i.elementsToInitialise.toLong()))
        is TableInstruction.TableCopyIsi -> context.tableCopy(i.srcTable.index, i.destTable.index, slot(i.srcOffsetSlot), immediate(i.dstOffset.toLong()), immediate(i.elementsToCopy.toLong()))
        is TableInstruction.TableInitIsi -> context.tableInit(i.table.index, i.element.index, slot(i.segmentOffsetSlot), immediate(i.tableOffset.toLong()), immediate(i.elementsToInitialise.toLong()))
        is TableInstruction.TableCopyIss -> context.tableCopy(i.srcTable.index, i.destTable.index, slot(i.srcOffsetSlot), slot(i.dstOffsetSlot), immediate(i.elementsToCopy.toLong()))
        is TableInstruction.TableInitIss -> context.tableInit(i.table.index, i.element.index, slot(i.segmentOffsetSlot), slot(i.tableOffsetSlot), immediate(i.elementsToInitialise.toLong()))
        is TableInstruction.TableCopySii -> context.tableCopy(i.srcTable.index, i.destTable.index, immediate(i.srcOffset.toLong()), immediate(i.dstOffset.toLong()), slot(i.elementsToCopySlot))
        is TableInstruction.TableInitSii -> context.tableInit(i.table.index, i.element.index, immediate(i.segmentOffset.toLong()), immediate(i.tableOffset.toLong()), slot(i.elementsToInitialiseSlot))
        is TableInstruction.TableCopySis -> context.tableCopy(i.srcTable.index, i.destTable.index, immediate(i.srcOffset.toLong()), slot(i.dstOffsetSlot), slot(i.elementsToCopySlot))
        is TableInstruction.TableInitSis -> context.tableInit(i.table.index, i.element.index, immediate(i.segmentOffset.toLong()), slot(i.tableOffsetSlot), slot(i.elementsToInitialiseSlot))
        is TableInstruction.TableCopySsi -> context.tableCopy(i.srcTable.index, i.destTable.index, slot(i.srcOffsetSlot), immediate(i.dstOffset.toLong()), slot(i.elementsToCopySlot))
        is TableInstruction.TableInitSsi -> context.tableInit(i.table.index, i.element.index, slot(i.segmentOffsetSlot), immediate(i.tableOffset.toLong()), slot(i.elementsToInitialiseSlot))
        is TableInstruction.TableCopySss -> context.tableCopy(i.srcTable.index, i.destTable.index, slot(i.srcOffsetSlot), slot(i.dstOffsetSlot), slot(i.elementsToCopySlot))
        is TableInstruction.TableInitSss -> context.tableInit(i.table.index, i.element.index, slot(i.segmentOffsetSlot), slot(i.tableOffsetSlot), slot(i.elementsToInitialiseSlot))
        is TableInstruction.ElemDrop -> {
            context.loadObject(Registers.OBJECT, Resource.ReferenceSegment(i.element.index), REFERENCE_SEGMENT)
            context.emit.invoke(MANAGED_ELEMENT_DROP, Registers.OBJECT)
        }
    }
}

private fun LowerContext.tableGet(
    table: Int,
    index: Operand,
    destination: Int,
) {
    loadObject(Registers.OBJECT, Resource.ReferenceTable(table), REFERENCE_TABLE)
    loadInt(Registers.I0, Registers.W0, index)
    emit.invoke(MANAGED_TABLE_GET, Registers.OBJECT, Registers.I0)
    emit.moveResultObject(Registers.SECOND_OBJECT)
    storeReference(destination, Registers.SECOND_OBJECT)
}

private fun LowerContext.tableSet(
    table: Int,
    index: Operand,
    valueSlot: Int,
) {
    loadObject(Registers.OBJECT, Resource.ReferenceTable(table), REFERENCE_TABLE)
    loadInt(Registers.I0, Registers.W0, index)
    loadReference(Registers.SECOND_OBJECT, valueSlot)
    emit.invoke(MANAGED_TABLE_SET, Registers.OBJECT, Registers.I0, Registers.SECOND_OBJECT)
}

private fun LowerContext.tableSize(
    table: Int,
    destination: Int,
) {
    loadObject(Registers.OBJECT, Resource.ReferenceTable(table), REFERENCE_TABLE)
    emit.invoke(MANAGED_TABLE_SIZE, Registers.OBJECT)
    emit.moveResult(Registers.I0)
    storeInt(destination, Registers.I0)
}

private fun LowerContext.tableGrow(
    table: Int,
    count: Operand,
    valueSlot: Int,
    destination: Int,
) {
    loadObject(Registers.OBJECT, Resource.ReferenceTable(table), REFERENCE_TABLE)
    loadInt(Registers.I0, Registers.W0, count)
    loadReference(Registers.SECOND_OBJECT, valueSlot)
    emit.invoke(MANAGED_TABLE_GROW, Registers.OBJECT, Registers.I0, Registers.SECOND_OBJECT)
    emit.moveResult(Registers.I0)
    storeInt(destination, Registers.I0)
}

private fun LowerContext.tableFill(
    table: Int,
    offset: Operand,
    valueSlot: Int,
    count: Operand,
) {
    loadObject(Registers.OBJECT, Resource.ReferenceTable(table), REFERENCE_TABLE)
    loadInt(Registers.I0, Registers.W0, offset)
    loadReference(Registers.SECOND_OBJECT, valueSlot)
    loadInt(Registers.I1, Registers.W1, count)
    emit.invoke(MANAGED_TABLE_FILL, Registers.OBJECT, Registers.I0, Registers.SECOND_OBJECT, Registers.I1)
}

private fun LowerContext.tableCopy(
    source: Int,
    destination: Int,
    sourceOffset: Operand,
    destinationOffset: Operand,
    count: Operand,
) {
    loadObject(Registers.OBJECT, Resource.ReferenceTable(source), REFERENCE_TABLE)
    loadObject(Registers.SECOND_OBJECT, Resource.ReferenceTable(destination), REFERENCE_TABLE)
    loadInt(Registers.I0, Registers.W0, sourceOffset)
    loadInt(Registers.I1, Registers.W1, destinationOffset)
    loadInt(Registers.I3, Registers.W0, count)
    emit.invoke(MANAGED_TABLE_COPY, Registers.OBJECT, Registers.SECOND_OBJECT, Registers.I0, Registers.I1, Registers.I3)
}

private fun LowerContext.tableInit(
    table: Int,
    element: Int,
    segmentOffset: Operand,
    tableOffset: Operand,
    count: Operand,
) {
    loadObject(Registers.OBJECT, Resource.ReferenceTable(table), REFERENCE_TABLE)
    loadObject(Registers.SECOND_OBJECT, Resource.ReferenceSegment(element), REFERENCE_SEGMENT)
    loadInt(Registers.I0, Registers.W0, segmentOffset)
    loadInt(Registers.I1, Registers.W1, tableOffset)
    loadInt(Registers.I3, Registers.W0, count)
    emit.invoke(MANAGED_TABLE_INIT, Registers.OBJECT, Registers.SECOND_OBJECT, Registers.I0, Registers.I1, Registers.I3)
}
