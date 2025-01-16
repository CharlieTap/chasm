package io.github.charlietap.chasm.fixture.executor.runtime.function

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.runtime.function.Expression
import io.github.charlietap.chasm.executor.runtime.function.Function
import io.github.charlietap.chasm.executor.runtime.function.Local

fun runtimeFunction(
    idx: Index.FunctionIndex = Index.FunctionIndex(0u),
    typeIndex: Index.TypeIndex = Index.TypeIndex(0u),
    locals: Array<Local> = emptyArray(),
    body: Expression = runtimeExpression(),
) = Function(
    idx = idx,
    typeIndex = typeIndex,
    locals = locals,
    body = body,
)
