package io.github.charlietap.chasm.ir.instruction

import io.github.charlietap.chasm.ir.module.Index.ElementIndex
import io.github.charlietap.chasm.ir.module.Index.TableIndex
import kotlin.jvm.JvmInline

sealed interface TableInstruction : Instruction {
    @JvmInline
    value class TableGet(val tableIdx: TableIndex) : TableInstruction

    @JvmInline
    value class TableSet(val tableIdx: TableIndex) : TableInstruction

    data class TableInit(val elemIdx: ElementIndex, val tableIdx: TableIndex) : TableInstruction

    @JvmInline
    value class ElemDrop(val elemIdx: ElementIndex) : TableInstruction

    data class TableCopy(val srcTableIdx: TableIndex, val destTableIdx: TableIndex) : TableInstruction

    @JvmInline
    value class TableGrow(val tableIdx: TableIndex) : TableInstruction

    @JvmInline
    value class TableSize(val tableIdx: TableIndex) : TableInstruction

    @JvmInline
    value class TableFill(val tableIdx: TableIndex) : TableInstruction
}
