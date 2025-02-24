package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Index.TableIndex
import io.github.charlietap.chasm.ast.module.Table
import io.github.charlietap.chasm.ir.instruction.Expression as IRExpression
import io.github.charlietap.chasm.ir.module.Index.TableIndex as IRTableIndex
import io.github.charlietap.chasm.ir.module.Table as IRTable

internal fun TableFactory(
    table: Table,
): IRTable = TableFactory(
    table = table,
    tableIndexFactory = ::TableIndexFactory,
    expressionFactory = ::ExpressionFactory,
)

internal inline fun TableFactory(
    table: Table,
    tableIndexFactory: IRFactory<TableIndex, IRTableIndex>,
    expressionFactory: IRFactory<Expression, IRExpression>,
): IRTable {
    return IRTable(
        idx = tableIndexFactory(table.idx),
        type = table.type,
        initExpression = expressionFactory(table.initExpression),
    )
}
