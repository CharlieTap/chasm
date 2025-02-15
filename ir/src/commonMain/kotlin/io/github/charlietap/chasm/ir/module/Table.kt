package io.github.charlietap.chasm.ir.module

import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.ir.type.TableType

data class Table(
    val idx: Index.TableIndex,
    val type: TableType,
    val initExpression: Expression,
)
