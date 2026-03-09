package io.github.charlietap.chasm.runtime.instruction

import io.github.charlietap.chasm.runtime.instance.ElementInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance

sealed interface FusedTableInstruction : LinkedInstruction {

    data class TableCopyIii(
        val elementsToCopy: Int,
        val srcOffset: Int,
        val dstOffset: Int,
        val srcTable: TableInstance,
        val destTable: TableInstance,
    ) : FusedTableInstruction

    data class TableCopyIis(
        val elementsToCopy: Int,
        val srcOffset: Int,
        val dstOffsetSlot: Int,
        val srcTable: TableInstance,
        val destTable: TableInstance,
    ) : FusedTableInstruction

    data class TableCopyIsi(
        val elementsToCopy: Int,
        val srcOffsetSlot: Int,
        val dstOffset: Int,
        val srcTable: TableInstance,
        val destTable: TableInstance,
    ) : FusedTableInstruction

    data class TableCopyIss(
        val elementsToCopy: Int,
        val srcOffsetSlot: Int,
        val dstOffsetSlot: Int,
        val srcTable: TableInstance,
        val destTable: TableInstance,
    ) : FusedTableInstruction

    data class TableCopySii(
        val elementsToCopySlot: Int,
        val srcOffset: Int,
        val dstOffset: Int,
        val srcTable: TableInstance,
        val destTable: TableInstance,
    ) : FusedTableInstruction

    data class TableCopySis(
        val elementsToCopySlot: Int,
        val srcOffset: Int,
        val dstOffsetSlot: Int,
        val srcTable: TableInstance,
        val destTable: TableInstance,
    ) : FusedTableInstruction

    data class TableCopySsi(
        val elementsToCopySlot: Int,
        val srcOffsetSlot: Int,
        val dstOffset: Int,
        val srcTable: TableInstance,
        val destTable: TableInstance,
    ) : FusedTableInstruction

    data class TableCopySss(
        val elementsToCopySlot: Int,
        val srcOffsetSlot: Int,
        val dstOffsetSlot: Int,
        val srcTable: TableInstance,
        val destTable: TableInstance,
    ) : FusedTableInstruction

    data class TableFillIii(
        val elementsToFill: Int,
        val fillValue: Long,
        val tableOffset: Int,
        val table: TableInstance,
    ) : FusedTableInstruction

    data class TableFillIis(
        val elementsToFill: Int,
        val fillValue: Long,
        val tableOffsetSlot: Int,
        val table: TableInstance,
    ) : FusedTableInstruction

    data class TableFillIsi(
        val elementsToFill: Int,
        val fillValueSlot: Int,
        val tableOffset: Int,
        val table: TableInstance,
    ) : FusedTableInstruction

    data class TableFillIss(
        val elementsToFill: Int,
        val fillValueSlot: Int,
        val tableOffsetSlot: Int,
        val table: TableInstance,
    ) : FusedTableInstruction

    data class TableFillSii(
        val elementsToFillSlot: Int,
        val fillValue: Long,
        val tableOffset: Int,
        val table: TableInstance,
    ) : FusedTableInstruction

    data class TableFillSis(
        val elementsToFillSlot: Int,
        val fillValue: Long,
        val tableOffsetSlot: Int,
        val table: TableInstance,
    ) : FusedTableInstruction

    data class TableFillSsi(
        val elementsToFillSlot: Int,
        val fillValueSlot: Int,
        val tableOffset: Int,
        val table: TableInstance,
    ) : FusedTableInstruction

    data class TableFillSss(
        val elementsToFillSlot: Int,
        val fillValueSlot: Int,
        val tableOffsetSlot: Int,
        val table: TableInstance,
    ) : FusedTableInstruction

    data class TableGrowIi(
        val elementsToAdd: Int,
        val referenceValue: Long,
        val destinationSlot: Int,
        val table: TableInstance,
        val max: Int,
    ) : FusedTableInstruction

    data class TableGrowIs(
        val elementsToAdd: Int,
        val referenceValueSlot: Int,
        val destinationSlot: Int,
        val table: TableInstance,
        val max: Int,
    ) : FusedTableInstruction

    data class TableGrowSi(
        val elementsToAddSlot: Int,
        val referenceValue: Long,
        val destinationSlot: Int,
        val table: TableInstance,
        val max: Int,
    ) : FusedTableInstruction

    data class TableGrowSs(
        val elementsToAddSlot: Int,
        val referenceValueSlot: Int,
        val destinationSlot: Int,
        val table: TableInstance,
        val max: Int,
    ) : FusedTableInstruction

    data class TableInitIii(
        val elementsToInitialise: Int,
        val segmentOffset: Int,
        val tableOffset: Int,
        val element: ElementInstance,
        val table: TableInstance,
    ) : FusedTableInstruction

    data class TableInitIis(
        val elementsToInitialise: Int,
        val segmentOffset: Int,
        val tableOffsetSlot: Int,
        val element: ElementInstance,
        val table: TableInstance,
    ) : FusedTableInstruction

    data class TableInitIsi(
        val elementsToInitialise: Int,
        val segmentOffsetSlot: Int,
        val tableOffset: Int,
        val element: ElementInstance,
        val table: TableInstance,
    ) : FusedTableInstruction

    data class TableInitIss(
        val elementsToInitialise: Int,
        val segmentOffsetSlot: Int,
        val tableOffsetSlot: Int,
        val element: ElementInstance,
        val table: TableInstance,
    ) : FusedTableInstruction

    data class TableInitSii(
        val elementsToInitialiseSlot: Int,
        val segmentOffset: Int,
        val tableOffset: Int,
        val element: ElementInstance,
        val table: TableInstance,
    ) : FusedTableInstruction

    data class TableInitSis(
        val elementsToInitialiseSlot: Int,
        val segmentOffset: Int,
        val tableOffsetSlot: Int,
        val element: ElementInstance,
        val table: TableInstance,
    ) : FusedTableInstruction

    data class TableInitSsi(
        val elementsToInitialiseSlot: Int,
        val segmentOffsetSlot: Int,
        val tableOffset: Int,
        val element: ElementInstance,
        val table: TableInstance,
    ) : FusedTableInstruction

    data class TableInitSss(
        val elementsToInitialiseSlot: Int,
        val segmentOffsetSlot: Int,
        val tableOffsetSlot: Int,
        val element: ElementInstance,
        val table: TableInstance,
    ) : FusedTableInstruction

    data class TableGetI(
        val elementIndex: Int,
        val destinationSlot: Int,
        val table: TableInstance,
    ) : FusedTableInstruction

    data class TableGetS(
        val elementIndexSlot: Int,
        val destinationSlot: Int,
        val table: TableInstance,
    ) : FusedTableInstruction

    data class TableSetIi(
        val value: Long,
        val elementIndex: Int,
        val table: TableInstance,
    ) : FusedTableInstruction

    data class TableSetIs(
        val value: Long,
        val elementIndexSlot: Int,
        val table: TableInstance,
    ) : FusedTableInstruction

    data class TableSetSi(
        val valueSlot: Int,
        val elementIndex: Int,
        val table: TableInstance,
    ) : FusedTableInstruction

    data class TableSetSs(
        val valueSlot: Int,
        val elementIndexSlot: Int,
        val table: TableInstance,
    ) : FusedTableInstruction

    data class TableSizeS(
        val destinationSlot: Int,
        val table: TableInstance,
    ) : FusedTableInstruction
}
