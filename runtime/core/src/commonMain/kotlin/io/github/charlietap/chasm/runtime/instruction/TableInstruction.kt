package io.github.charlietap.chasm.runtime.instruction

import io.github.charlietap.chasm.runtime.instance.ElementInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import kotlin.jvm.JvmInline

sealed interface TableInstruction : LinkedInstruction {

    data class TableCopyIii(
        val elementsToCopy: Int,
        val srcOffset: Int,
        val dstOffset: Int,
        val srcTable: TableInstance,
        val destTable: TableInstance,
    ) : TableInstruction

    data class TableCopyIis(
        val elementsToCopy: Int,
        val srcOffset: Int,
        val dstOffsetSlot: Int,
        val srcTable: TableInstance,
        val destTable: TableInstance,
    ) : TableInstruction

    data class TableCopyIsi(
        val elementsToCopy: Int,
        val srcOffsetSlot: Int,
        val dstOffset: Int,
        val srcTable: TableInstance,
        val destTable: TableInstance,
    ) : TableInstruction

    data class TableCopyIss(
        val elementsToCopy: Int,
        val srcOffsetSlot: Int,
        val dstOffsetSlot: Int,
        val srcTable: TableInstance,
        val destTable: TableInstance,
    ) : TableInstruction

    data class TableCopySii(
        val elementsToCopySlot: Int,
        val srcOffset: Int,
        val dstOffset: Int,
        val srcTable: TableInstance,
        val destTable: TableInstance,
    ) : TableInstruction

    data class TableCopySis(
        val elementsToCopySlot: Int,
        val srcOffset: Int,
        val dstOffsetSlot: Int,
        val srcTable: TableInstance,
        val destTable: TableInstance,
    ) : TableInstruction

    data class TableCopySsi(
        val elementsToCopySlot: Int,
        val srcOffsetSlot: Int,
        val dstOffset: Int,
        val srcTable: TableInstance,
        val destTable: TableInstance,
    ) : TableInstruction

    data class TableCopySss(
        val elementsToCopySlot: Int,
        val srcOffsetSlot: Int,
        val dstOffsetSlot: Int,
        val srcTable: TableInstance,
        val destTable: TableInstance,
    ) : TableInstruction

    data class TableFillIsi(
        val elementsToFill: Int,
        val fillValueSlot: Int,
        val tableOffset: Int,
        val table: TableInstance,
    ) : TableInstruction

    data class TableFillIss(
        val elementsToFill: Int,
        val fillValueSlot: Int,
        val tableOffsetSlot: Int,
        val table: TableInstance,
    ) : TableInstruction

    data class TableFillSsi(
        val elementsToFillSlot: Int,
        val fillValueSlot: Int,
        val tableOffset: Int,
        val table: TableInstance,
    ) : TableInstruction

    data class TableFillSss(
        val elementsToFillSlot: Int,
        val fillValueSlot: Int,
        val tableOffsetSlot: Int,
        val table: TableInstance,
    ) : TableInstruction

    data class TableGrowIs(
        val elementsToAdd: Int,
        val referenceValueSlot: Int,
        val destinationSlot: Int,
        val table: TableInstance,
        val max: Int,
    ) : TableInstruction

    data class TableGrowSs(
        val elementsToAddSlot: Int,
        val referenceValueSlot: Int,
        val destinationSlot: Int,
        val table: TableInstance,
        val max: Int,
    ) : TableInstruction

    data class TableInitIii(
        val elementsToInitialise: Int,
        val segmentOffset: Int,
        val tableOffset: Int,
        val element: ElementInstance,
        val table: TableInstance,
    ) : TableInstruction

    data class TableInitIis(
        val elementsToInitialise: Int,
        val segmentOffset: Int,
        val tableOffsetSlot: Int,
        val element: ElementInstance,
        val table: TableInstance,
    ) : TableInstruction

    data class TableInitIsi(
        val elementsToInitialise: Int,
        val segmentOffsetSlot: Int,
        val tableOffset: Int,
        val element: ElementInstance,
        val table: TableInstance,
    ) : TableInstruction

    data class TableInitIss(
        val elementsToInitialise: Int,
        val segmentOffsetSlot: Int,
        val tableOffsetSlot: Int,
        val element: ElementInstance,
        val table: TableInstance,
    ) : TableInstruction

    data class TableInitSii(
        val elementsToInitialiseSlot: Int,
        val segmentOffset: Int,
        val tableOffset: Int,
        val element: ElementInstance,
        val table: TableInstance,
    ) : TableInstruction

    data class TableInitSis(
        val elementsToInitialiseSlot: Int,
        val segmentOffset: Int,
        val tableOffsetSlot: Int,
        val element: ElementInstance,
        val table: TableInstance,
    ) : TableInstruction

    data class TableInitSsi(
        val elementsToInitialiseSlot: Int,
        val segmentOffsetSlot: Int,
        val tableOffset: Int,
        val element: ElementInstance,
        val table: TableInstance,
    ) : TableInstruction

    data class TableInitSss(
        val elementsToInitialiseSlot: Int,
        val segmentOffsetSlot: Int,
        val tableOffsetSlot: Int,
        val element: ElementInstance,
        val table: TableInstance,
    ) : TableInstruction

    data class TableGetI(
        val elementIndex: Int,
        val destinationSlot: Int,
        val table: TableInstance,
    ) : TableInstruction

    data class TableGetS(
        val elementIndexSlot: Int,
        val destinationSlot: Int,
        val table: TableInstance,
    ) : TableInstruction

    data class TableSetSi(
        val valueSlot: Int,
        val elementIndex: Int,
        val table: TableInstance,
    ) : TableInstruction

    data class TableSetSs(
        val valueSlot: Int,
        val elementIndexSlot: Int,
        val table: TableInstance,
    ) : TableInstruction

    data class TableSizeS(
        val destinationSlot: Int,
        val table: TableInstance,
    ) : TableInstruction

    @JvmInline
    value class ElemDrop(val element: ElementInstance) : TableInstruction
}
