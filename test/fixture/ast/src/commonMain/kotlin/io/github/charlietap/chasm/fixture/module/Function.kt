package io.github.charlietap.chasm.fixture.module

import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.ast.module.Local

fun function(
    idx: Index.FunctionIndex = Index.FunctionIndex(0u),
    typeIndex: Index.TypeIndex = Index.TypeIndex(0u),
    locals: List<Local> = emptyList(),
    body: Expression = Expression(emptyList()),
) = Function(
    idx = idx,
    typeIndex = typeIndex,
    locals = locals,
    body = body,
)
