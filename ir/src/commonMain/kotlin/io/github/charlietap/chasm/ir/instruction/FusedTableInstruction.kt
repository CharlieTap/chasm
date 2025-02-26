package io.github.charlietap.chasm.ir.instruction

import io.github.charlietap.chasm.ir.module.Index.ElementIndex
import io.github.charlietap.chasm.ir.module.Index.TableIndex

sealed interface FusedTableInstruction : Instruction {

    data class TableGet(
        val elementIndex: FusedOperand,
        val destination: FusedDestination,
        val tableIdx: TableIndex,
    ) : FusedTableInstruction

    data class TableCopy(
        val elementsToCopy: FusedOperand,
        val srcOffset: FusedOperand,
        val dstOffset: FusedOperand,
        val srcTableIdx: TableIndex,
        val destTableIdx: TableIndex,
    ) : FusedTableInstruction

    data class TableFill(
        val elementsToFill: FusedOperand,
        val fillValue: FusedOperand,
        val tableOffset: FusedOperand,
        val tableIdx: TableIndex,
    ) : FusedTableInstruction

    data class TableGrow(
        val elementsToAdd: FusedOperand,
        val referenceValue: FusedOperand,
        val destination: FusedDestination,
        val tableIdx: TableIndex,
    ) : FusedTableInstruction

    data class TableInit(
        val elementsToInitialise: FusedOperand,
        val segmentOffset: FusedOperand,
        val tableOffset: FusedOperand,
        val elemIdx: ElementIndex,
        val tableIdx: TableIndex,
    ) : FusedTableInstruction
}
