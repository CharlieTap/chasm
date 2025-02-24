package io.github.charlietap.chasm.fixture.ir.module

import io.github.charlietap.chasm.fixture.ir.instruction.expression
import io.github.charlietap.chasm.fixture.type.globalType
import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.ir.module.Global
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.type.GlobalType

fun global(
    idx: Index.GlobalIndex = globalIndex(),
    type: GlobalType = globalType(),
    initExpression: Expression = expression(),
) = Global(
    idx = idx,
    type = type,
    initExpression = initExpression,
)
