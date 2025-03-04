package io.github.charlietap.chasm.ast.module

import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Index.TableIndex
import io.github.charlietap.chasm.type.TableType

data class Table(
    val idx: TableIndex,
    val type: TableType,
    val initExpression: Expression,
)
