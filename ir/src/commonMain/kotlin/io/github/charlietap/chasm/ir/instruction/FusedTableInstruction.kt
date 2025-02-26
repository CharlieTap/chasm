package io.github.charlietap.chasm.ir.instruction

import io.github.charlietap.chasm.ir.module.Index.TableIndex

sealed interface FusedTableInstruction : Instruction {

    data class TableCopy(
        val elementsToCopy: FusedOperand,
        val srcOffset: FusedOperand,
        val dstOffset: FusedOperand,
        val srcTableIdx: TableIndex,
        val destTableIdx: TableIndex,
    ) : FusedTableInstruction
}
