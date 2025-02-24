package io.github.charlietap.chasm.fixture.ir.module

import io.github.charlietap.chasm.fixture.ir.instruction.expression
import io.github.charlietap.chasm.fixture.type.tableType
import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.ir.module.Table
import io.github.charlietap.chasm.type.TableType

fun table(
    idx: Index.TableIndex = tableIndex(),
    type: TableType = tableType(),
    initExpression: Expression = expression(),
) = Table(
    idx = idx,
    type = type,
    initExpression = initExpression,
)
