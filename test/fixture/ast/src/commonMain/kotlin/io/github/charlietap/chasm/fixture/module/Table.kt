package io.github.charlietap.chasm.fixture.module

import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.module.Table
import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.fixture.instruction.expression
import io.github.charlietap.chasm.fixture.type.tableType

fun table(
    idx: Index.TableIndex = Index.TableIndex(0u),
    type: TableType = tableType(),
    initExpression: Expression = expression(),
) = Table(
    idx = idx,
    type = type,
    initExpression = initExpression,
)
