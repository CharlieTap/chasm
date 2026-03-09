package io.github.charlietap.chasm.fixture.runtime.function

import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.runtime.function.Expression
import io.github.charlietap.chasm.runtime.function.Function

fun runtimeFunction(
    idx: Index.FunctionIndex = Index.FunctionIndex(0),
    typeIndex: Index.TypeIndex = Index.TypeIndex(0),
    locals: LongArray = longArrayOf(),
    body: Expression = runtimeExpression(),
    frameSlots: Int = 0,
    frameSlotMode: Boolean = false,
    returnSlots: List<Int> = emptyList(),
) = Function(
    idx = idx,
    typeIndex = typeIndex,
    locals = locals,
    body = body,
    frameSlots = frameSlots,
    frameSlotMode = frameSlotMode,
    returnSlots = returnSlots,
)
