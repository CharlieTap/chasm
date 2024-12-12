package io.github.charlietap.chasm.fixture.ast.module

import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Global
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.fixture.ast.instruction.expression
import io.github.charlietap.chasm.fixture.ast.type.globalType

fun global(
    idx: Index.GlobalIndex = Index.GlobalIndex(0u),
    type: GlobalType = globalType(),
    initExpression: Expression = expression(),
) = Global(
    idx = idx,
    type = type,
    initExpression = initExpression,
)
