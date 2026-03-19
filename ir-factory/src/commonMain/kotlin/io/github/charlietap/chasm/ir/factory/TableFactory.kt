package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Index.TableIndex
import io.github.charlietap.chasm.ast.module.Table
import io.github.charlietap.chasm.type.TableType
import io.github.charlietap.chasm.type.rolling.substitution.Substitution
import io.github.charlietap.chasm.type.rolling.substitution.TableTypeSubstitutor
import io.github.charlietap.chasm.type.rolling.substitution.TypeSubstitutor
import io.github.charlietap.chasm.ir.instruction.Expression as IRExpression
import io.github.charlietap.chasm.ir.module.Index.TableIndex as IRTableIndex
import io.github.charlietap.chasm.ir.module.Table as IRTable

internal typealias TableFactory = (Table, Substitution) -> IRTable

internal fun TableFactory(
    table: Table,
    substitution: Substitution,
): IRTable = TableFactory(
    table = table,
    substitution = substitution,
    tableIndexFactory = ::TableIndexFactory,
    expressionFactory = ::ExpressionFactory,
    tableTypeSubstitutor = ::TableTypeSubstitutor,
)

internal inline fun TableFactory(
    table: Table,
    substitution: Substitution,
    tableIndexFactory: IRFactory<TableIndex, IRTableIndex>,
    expressionFactory: IRFactory<Expression, IRExpression>,
    tableTypeSubstitutor: TypeSubstitutor<TableType>,
): IRTable {
    return IRTable(
        idx = tableIndexFactory(table.idx),
        type = tableTypeSubstitutor(table.type, substitution),
        initExpression = expressionFactory(table.initExpression),
    )
}
