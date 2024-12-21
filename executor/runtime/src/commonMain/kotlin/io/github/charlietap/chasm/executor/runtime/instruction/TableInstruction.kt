package io.github.charlietap.chasm.executor.runtime.instruction

import io.github.charlietap.chasm.ast.module.Index.ElementIndex
import io.github.charlietap.chasm.ast.module.Index.TableIndex
import io.github.charlietap.chasm.executor.runtime.instance.TableInstance
import kotlin.jvm.JvmInline

sealed interface TableInstruction : ExecutionInstruction {
    @JvmInline
    value class TableGet(val table: TableInstance) : TableInstruction

    @JvmInline
    value class TableSet(val table: TableInstance) : TableInstruction

    data class TableInit(val elemIdx: ElementIndex, val table: TableInstance) : TableInstruction

    @JvmInline
    value class ElemDrop(val elemIdx: ElementIndex) : TableInstruction

    data class TableCopy(val srcTable: TableInstance, val destTable: TableInstance) : TableInstruction

    @JvmInline
    value class TableGrow(val table: TableInstance) : TableInstruction

    @JvmInline
    value class TableSize(val tableIdx: TableIndex) : TableInstruction

    @JvmInline
    value class TableFill(val tableIdx: TableIndex) : TableInstruction
}
