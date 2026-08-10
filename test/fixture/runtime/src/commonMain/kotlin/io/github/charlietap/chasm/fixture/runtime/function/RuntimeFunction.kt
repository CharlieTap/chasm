package io.github.charlietap.chasm.fixture.runtime.function

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.runtime.function.Expression
import io.github.charlietap.chasm.runtime.function.Function

fun runtimeFunction(
    idx: Index.FunctionIndex = Index.FunctionIndex(0u),
    typeIndex: Index.TypeIndex = Index.TypeIndex(0u),
    locals: LongArray = longArrayOf(),
    body: Expression = runtimeExpression(),
    frameSlots: Int = 0,
    returnSlots: IntArray = intArrayOf(),
    collectGarbageAfterInvocation: Boolean = false,
) = Function(
    idx = idx,
    typeIndex = typeIndex,
    locals = locals,
    body = body,
    frameSlots = frameSlots,
    returnSlots = returnSlots,
    collectGarbageAfterInvocation = collectGarbageAfterInvocation,
)
