package io.github.charlietap.chasm.executor.runtime.instruction

import io.github.charlietap.chasm.executor.runtime.instance.ElementInstance
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

    data class TableGrow(
        val elementsToAdd: LoadOp,
        val referenceValue: LoadOp,
        val destination: StoreOp,
        val table: TableInstance,
        val max: Int,
    ) : FusedTableInstruction

    data class TableInit(
        val elementsToInitialise: LoadOp,
        val segmentOffset: LoadOp,
        val tableOffset: LoadOp,
        val element: ElementInstance,
        val table: TableInstance,
    ) : FusedTableInstruction

    data class TableGet(
        val elementIndex: LoadOp,
        val destination: StoreOp,
        val table: TableInstance,
    ) : FusedTableInstruction

    data class TableSet(
        val value: LoadOp,
        val elementIndex: LoadOp,
        val table: TableInstance,
    ) : FusedTableInstruction

    data class TableSize(
        val destination: StoreOp,
        val table: TableInstance,
    ) : FusedTableInstruction
}
