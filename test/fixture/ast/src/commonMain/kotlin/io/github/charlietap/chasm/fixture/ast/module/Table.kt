package io.github.charlietap.chasm.fixture.ast.module

import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Table
import io.github.charlietap.chasm.fixture.ast.instruction.expression
import io.github.charlietap.chasm.fixture.type.tableType
import io.github.charlietap.chasm.type.TableType

fun table(
    idx: Index.TableIndex = Index.TableIndex(0u),
    type: TableType = tableType(),
    initExpression: Expression = expression(),
) = Table(
    idx = idx,
    type = type,
    initExpression = initExpression,
)
