package io.github.charlietap.chasm.executor.runtime.instruction

import io.github.charlietap.chasm.executor.runtime.instance.TableInstance

sealed interface FusedTableInstruction : LinkedInstruction {

    data class TableCopy(
        val elementsToCopy: LoadOp,
        val srcOffset: LoadOp,
        val dstOffset: LoadOp,
        val srcTable: TableInstance,
        val destTable: TableInstance,
    ) : FusedTableInstruction

    data class TableFill(
        val elementsToFill: LoadOp,
        val fillValue: LoadOp,
        val tableOffset: LoadOp,
        val table: TableInstance,
    ) : FusedTableInstruction
}
