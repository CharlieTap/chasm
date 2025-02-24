package io.github.charlietap.chasm.fixture.ast.module

import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Global
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.fixture.ast.instruction.expression
import io.github.charlietap.chasm.fixture.type.globalType
import io.github.charlietap.chasm.type.GlobalType

fun global(
    idx: Index.GlobalIndex = Index.GlobalIndex(0u),
    type: GlobalType = globalType(),
    initExpression: Expression = expression(),
) = Global(
    idx = idx,
    type = type,
    initExpression = initExpression,
)
