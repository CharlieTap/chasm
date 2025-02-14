package io.github.charlietap.chasm.executor.runtime.instruction

import io.github.charlietap.chasm.executor.runtime.instance.ElementInstance
import io.github.charlietap.chasm.executor.runtime.instance.TableInstance
import kotlin.jvm.JvmInline

sealed interface TableInstruction : LinkedInstruction {
    @JvmInline
    value class TableGet(val table: TableInstance) : TableInstruction

    @JvmInline
    value class TableSet(val table: TableInstance) : TableInstruction

    data class TableInit(val element: ElementInstance, val table: TableInstance) : TableInstruction

    @JvmInline
    value class ElemDrop(val element: ElementInstance) : TableInstruction

    data class TableCopy(val srcTable: TableInstance, val destTable: TableInstance) : TableInstruction

    data class TableGrow(
        val table: TableInstance,
        val max: Int,
    ) : TableInstruction

    @JvmInline
    value class TableSize(val table: TableInstance) : TableInstruction

    @JvmInline
    value class TableFill(val table: TableInstance) : TableInstruction
}
