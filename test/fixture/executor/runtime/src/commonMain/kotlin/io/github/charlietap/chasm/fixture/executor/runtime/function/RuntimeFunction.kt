package io.github.charlietap.chasm.fixture.executor.runtime.function

import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.runtime.function.Expression
import io.github.charlietap.chasm.runtime.function.Function

fun runtimeFunction(
    idx: Index.FunctionIndex = Index.FunctionIndex(0),
    typeIndex: Index.TypeIndex = Index.TypeIndex(0),
    locals: LongArray = longArrayOf(),
    body: Expression = runtimeExpression(),
) = Function(
    idx = idx,
    typeIndex = typeIndex,
    locals = locals,
    body = body,
)
