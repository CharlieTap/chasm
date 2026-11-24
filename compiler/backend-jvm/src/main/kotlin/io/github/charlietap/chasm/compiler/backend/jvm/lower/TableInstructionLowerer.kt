package io.github.charlietap.chasm.compiler.backend.jvm.lower

import io.github.charlietap.chasm.cir.TableInstruction
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_ELEMENT_DROP
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_TABLE_COPY
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_TABLE_FILL
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_TABLE_GET
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_TABLE_GROW
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_TABLE_INIT
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_TABLE_SET
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_TABLE_SIZE
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.compiler.backend.analysis.form.immediate
import io.github.charlietap.chasm.compiler.backend.analysis.form.slot

internal fun lowerTableInstruction(
    instruction: TableInstruction,
    context: LowerContext,
) {
    when (val i = instruction) {
        is TableInstruction.TableGetI -> context.tableGet(i.table.index, immediate(i.elementIndex), i.destinationSlot)
        is TableInstruction.TableGetS -> context.tableGet(i.table.index, slot(i.elementIndexSlot), i.destinationSlot)
        is TableInstruction.TableSetSi -> context.tableSet(i.table.index, immediate(i.elementIndex), i.valueSlot)
        is TableInstruction.TableSetSs -> context.tableSet(i.table.index, slot(i.elementIndexSlot), i.valueSlot)
        is TableInstruction.TableSizeS -> {
            context.pushResource(Resource.ReferenceTable(i.table.index))
            context.emit.invokeStatic(MANAGED_TABLE_SIZE)
            context.storeInt(i.destinationSlot)
        }
        is TableInstruction.TableGrowIs -> context.tableGrow(i.table.index, immediate(i.elementsToAdd), i.referenceValueSlot, i.destinationSlot)
        is TableInstruction.TableGrowSs -> context.tableGrow(i.table.index, slot(i.elementsToAddSlot), i.referenceValueSlot, i.destinationSlot)
        is TableInstruction.TableFillIsi -> context.tableFill(i.table.index, immediate(i.tableOffset), i.fillValueSlot, immediate(i.elementsToFill))
        is TableInstruction.TableFillIss -> context.tableFill(i.table.index, slot(i.tableOffsetSlot), i.fillValueSlot, immediate(i.elementsToFill))
        is TableInstruction.TableFillSsi -> context.tableFill(i.table.index, immediate(i.tableOffset), i.fillValueSlot, slot(i.elementsToFillSlot))
        is TableInstruction.TableFillSss -> context.tableFill(i.table.index, slot(i.tableOffsetSlot), i.fillValueSlot, slot(i.elementsToFillSlot))
        is TableInstruction.TableCopyIii ->
            context.tableCopy(i.srcTable.index, i.destTable.index, immediate(i.srcOffset), immediate(i.dstOffset), immediate(i.elementsToCopy))
        is TableInstruction.TableCopyIis ->
            context.tableCopy(i.srcTable.index, i.destTable.index, immediate(i.srcOffset), slot(i.dstOffsetSlot), immediate(i.elementsToCopy))
        is TableInstruction.TableCopyIsi ->
            context.tableCopy(i.srcTable.index, i.destTable.index, slot(i.srcOffsetSlot), immediate(i.dstOffset), immediate(i.elementsToCopy))
        is TableInstruction.TableCopyIss ->
            context.tableCopy(i.srcTable.index, i.destTable.index, slot(i.srcOffsetSlot), slot(i.dstOffsetSlot), immediate(i.elementsToCopy))
        is TableInstruction.TableCopySii ->
            context.tableCopy(i.srcTable.index, i.destTable.index, immediate(i.srcOffset), immediate(i.dstOffset), slot(i.elementsToCopySlot))
        is TableInstruction.TableCopySis ->
            context.tableCopy(i.srcTable.index, i.destTable.index, immediate(i.srcOffset), slot(i.dstOffsetSlot), slot(i.elementsToCopySlot))
        is TableInstruction.TableCopySsi ->
            context.tableCopy(i.srcTable.index, i.destTable.index, slot(i.srcOffsetSlot), immediate(i.dstOffset), slot(i.elementsToCopySlot))
        is TableInstruction.TableCopySss ->
            context.tableCopy(i.srcTable.index, i.destTable.index, slot(i.srcOffsetSlot), slot(i.dstOffsetSlot), slot(i.elementsToCopySlot))
        is TableInstruction.TableInitIii ->
            context.tableInit(i.table.index, i.element.index, immediate(i.segmentOffset), immediate(i.tableOffset), immediate(i.elementsToInitialise))
        is TableInstruction.TableInitIis ->
            context.tableInit(i.table.index, i.element.index, immediate(i.segmentOffset), slot(i.tableOffsetSlot), immediate(i.elementsToInitialise))
        is TableInstruction.TableInitIsi ->
            context.tableInit(i.table.index, i.element.index, slot(i.segmentOffsetSlot), immediate(i.tableOffset), immediate(i.elementsToInitialise))
        is TableInstruction.TableInitIss ->
            context.tableInit(i.table.index, i.element.index, slot(i.segmentOffsetSlot), slot(i.tableOffsetSlot), immediate(i.elementsToInitialise))
        is TableInstruction.TableInitSii ->
            context.tableInit(i.table.index, i.element.index, immediate(i.segmentOffset), immediate(i.tableOffset), slot(i.elementsToInitialiseSlot))
        is TableInstruction.TableInitSis ->
            context.tableInit(i.table.index, i.element.index, immediate(i.segmentOffset), slot(i.tableOffsetSlot), slot(i.elementsToInitialiseSlot))
        is TableInstruction.TableInitSsi ->
            context.tableInit(i.table.index, i.element.index, slot(i.segmentOffsetSlot), immediate(i.tableOffset), slot(i.elementsToInitialiseSlot))
        is TableInstruction.TableInitSss ->
            context.tableInit(i.table.index, i.element.index, slot(i.segmentOffsetSlot), slot(i.tableOffsetSlot), slot(i.elementsToInitialiseSlot))
        is TableInstruction.ElemDrop -> {
            context.pushResource(Resource.ReferenceSegment(i.element.index))
            context.emit.invokeStatic(MANAGED_ELEMENT_DROP)
        }
    }
}

private fun LowerContext.tableGet(
    table: Int,
    index: Operand,
    destination: Int,
) {
    pushResource(Resource.ReferenceTable(table))
    pushInt(index)
    emit.invokeStatic(MANAGED_TABLE_GET)
    storeObject(destination)
}

private fun LowerContext.tableSet(
    table: Int,
    index: Operand,
    valueSlot: Int,
) {
    pushResource(Resource.ReferenceTable(table))
    pushInt(index)
    pushObject(slot(valueSlot))
    emit.invokeStatic(MANAGED_TABLE_SET)
}

private fun LowerContext.tableGrow(
    table: Int,
    count: Operand,
    valueSlot: Int,
    destination: Int,
) {
    pushResource(Resource.ReferenceTable(table))
    pushInt(count)
    pushObject(slot(valueSlot))
    emit.invokeStatic(MANAGED_TABLE_GROW)
    storeInt(destination)
}

private fun LowerContext.tableFill(
    table: Int,
    offset: Operand,
    valueSlot: Int,
    count: Operand,
) {
    pushResource(Resource.ReferenceTable(table))
    pushInt(offset)
    pushObject(slot(valueSlot))
    pushInt(count)
    emit.invokeStatic(MANAGED_TABLE_FILL)
}

private fun LowerContext.tableCopy(
    source: Int,
    destination: Int,
    sourceOffset: Operand,
    destinationOffset: Operand,
    count: Operand,
) {
    pushResource(Resource.ReferenceTable(source))
    pushResource(Resource.ReferenceTable(destination))
    pushInt(sourceOffset)
    pushInt(destinationOffset)
    pushInt(count)
    emit.invokeStatic(MANAGED_TABLE_COPY)
}

private fun LowerContext.tableInit(
    table: Int,
    element: Int,
    segmentOffset: Operand,
    tableOffset: Operand,
    count: Operand,
) {
    pushResource(Resource.ReferenceTable(table))
    pushResource(Resource.ReferenceSegment(element))
    pushInt(segmentOffset)
    pushInt(tableOffset)
    pushInt(count)
    emit.invokeStatic(MANAGED_TABLE_INIT)
}
