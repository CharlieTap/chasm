package io.github.charlietap.chasm.runtime.instruction

import io.github.charlietap.chasm.runtime.instance.ElementInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance

sealed interface TableSuperInstruction : LinkedInstruction {

    data class TableCopyIii(
        val elementsToCopy: Int,
        val srcOffset: Int,
        val dstOffset: Int,
        val srcTable: TableInstance,
        val destTable: TableInstance,
    ) : TableSuperInstruction

    data class TableCopyIis(
        val elementsToCopy: Int,
        val srcOffset: Int,
        val dstOffsetSlot: Int,
        val srcTable: TableInstance,
        val destTable: TableInstance,
    ) : TableSuperInstruction

    data class TableCopyIsi(
        val elementsToCopy: Int,
        val srcOffsetSlot: Int,
        val dstOffset: Int,
        val srcTable: TableInstance,
        val destTable: TableInstance,
    ) : TableSuperInstruction

    data class TableCopyIss(
        val elementsToCopy: Int,
        val srcOffsetSlot: Int,
        val dstOffsetSlot: Int,
        val srcTable: TableInstance,
        val destTable: TableInstance,
    ) : TableSuperInstruction

    data class TableCopySii(
        val elementsToCopySlot: Int,
        val srcOffset: Int,
        val dstOffset: Int,
        val srcTable: TableInstance,
        val destTable: TableInstance,
    ) : TableSuperInstruction

    data class TableCopySis(
        val elementsToCopySlot: Int,
        val srcOffset: Int,
        val dstOffsetSlot: Int,
        val srcTable: TableInstance,
        val destTable: TableInstance,
    ) : TableSuperInstruction

    data class TableCopySsi(
        val elementsToCopySlot: Int,
        val srcOffsetSlot: Int,
        val dstOffset: Int,
        val srcTable: TableInstance,
        val destTable: TableInstance,
    ) : TableSuperInstruction

    data class TableCopySss(
        val elementsToCopySlot: Int,
        val srcOffsetSlot: Int,
        val dstOffsetSlot: Int,
        val srcTable: TableInstance,
        val destTable: TableInstance,
    ) : TableSuperInstruction

    data class TableFillIii(
        val elementsToFill: Int,
        val fillValue: Long,
        val tableOffset: Int,
        val table: TableInstance,
    ) : TableSuperInstruction

    data class TableFillIis(
        val elementsToFill: Int,
        val fillValue: Long,
        val tableOffsetSlot: Int,
        val table: TableInstance,
    ) : TableSuperInstruction

    data class TableFillIsi(
        val elementsToFill: Int,
        val fillValueSlot: Int,
        val tableOffset: Int,
        val table: TableInstance,
    ) : TableSuperInstruction

    data class TableFillIss(
        val elementsToFill: Int,
        val fillValueSlot: Int,
        val tableOffsetSlot: Int,
        val table: TableInstance,
    ) : TableSuperInstruction

    data class TableFillSii(
        val elementsToFillSlot: Int,
        val fillValue: Long,
        val tableOffset: Int,
        val table: TableInstance,
    ) : TableSuperInstruction

    data class TableFillSis(
        val elementsToFillSlot: Int,
        val fillValue: Long,
        val tableOffsetSlot: Int,
        val table: TableInstance,
    ) : TableSuperInstruction

    data class TableFillSsi(
        val elementsToFillSlot: Int,
        val fillValueSlot: Int,
        val tableOffset: Int,
        val table: TableInstance,
    ) : TableSuperInstruction

    data class TableFillSss(
        val elementsToFillSlot: Int,
        val fillValueSlot: Int,
        val tableOffsetSlot: Int,
        val table: TableInstance,
    ) : TableSuperInstruction

    data class TableGrowIi(
        val elementsToAdd: Int,
        val referenceValue: Long,
        val destinationSlot: Int,
        val table: TableInstance,
        val max: Int,
    ) : TableSuperInstruction

    data class TableGrowIs(
        val elementsToAdd: Int,
        val referenceValueSlot: Int,
        val destinationSlot: Int,
        val table: TableInstance,
        val max: Int,
    ) : TableSuperInstruction

    data class TableGrowSi(
        val elementsToAddSlot: Int,
        val referenceValue: Long,
        val destinationSlot: Int,
        val table: TableInstance,
        val max: Int,
    ) : TableSuperInstruction

    data class TableGrowSs(
        val elementsToAddSlot: Int,
        val referenceValueSlot: Int,
        val destinationSlot: Int,
        val table: TableInstance,
        val max: Int,
    ) : TableSuperInstruction

    data class TableInitIii(
        val elementsToInitialise: Int,
        val segmentOffset: Int,
        val tableOffset: Int,
        val element: ElementInstance,
        val table: TableInstance,
    ) : TableSuperInstruction

    data class TableInitIis(
        val elementsToInitialise: Int,
        val segmentOffset: Int,
        val tableOffsetSlot: Int,
        val element: ElementInstance,
        val table: TableInstance,
    ) : TableSuperInstruction

    data class TableInitIsi(
        val elementsToInitialise: Int,
        val segmentOffsetSlot: Int,
        val tableOffset: Int,
        val element: ElementInstance,
        val table: TableInstance,
    ) : TableSuperInstruction

    data class TableInitIss(
        val elementsToInitialise: Int,
        val segmentOffsetSlot: Int,
        val tableOffsetSlot: Int,
        val element: ElementInstance,
        val table: TableInstance,
    ) : TableSuperInstruction

    data class TableInitSii(
        val elementsToInitialiseSlot: Int,
        val segmentOffset: Int,
        val tableOffset: Int,
        val element: ElementInstance,
        val table: TableInstance,
    ) : TableSuperInstruction

    data class TableInitSis(
        val elementsToInitialiseSlot: Int,
        val segmentOffset: Int,
        val tableOffsetSlot: Int,
        val element: ElementInstance,
        val table: TableInstance,
    ) : TableSuperInstruction

    data class TableInitSsi(
        val elementsToInitialiseSlot: Int,
        val segmentOffsetSlot: Int,
        val tableOffset: Int,
        val element: ElementInstance,
        val table: TableInstance,
    ) : TableSuperInstruction

    data class TableInitSss(
        val elementsToInitialiseSlot: Int,
        val segmentOffsetSlot: Int,
        val tableOffsetSlot: Int,
        val element: ElementInstance,
        val table: TableInstance,
    ) : TableSuperInstruction

    data class TableGetI(
        val elementIndex: Int,
        val destinationSlot: Int,
        val table: TableInstance,
    ) : TableSuperInstruction

    data class TableGetS(
        val elementIndexSlot: Int,
        val destinationSlot: Int,
        val table: TableInstance,
    ) : TableSuperInstruction

    data class TableSetIi(
        val value: Long,
        val elementIndex: Int,
        val table: TableInstance,
    ) : TableSuperInstruction

    data class TableSetIs(
        val value: Long,
        val elementIndexSlot: Int,
        val table: TableInstance,
    ) : TableSuperInstruction

    data class TableSetSi(
        val valueSlot: Int,
        val elementIndex: Int,
        val table: TableInstance,
    ) : TableSuperInstruction

    data class TableSetSs(
        val valueSlot: Int,
        val elementIndexSlot: Int,
        val table: TableInstance,
    ) : TableSuperInstruction

    data class TableSizeS(
        val destinationSlot: Int,
        val table: TableInstance,
    ) : TableSuperInstruction
}
