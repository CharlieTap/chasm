package io.github.charlietap.chasm.fixture.ir.module

import io.github.charlietap.chasm.fixture.ir.instruction.expression
import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.ir.module.Function
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.ir.module.Local

fun function(
    idx: Index.FunctionIndex = functionIndex(),
    typeIndex: Index.TypeIndex = typeIndex(),
    locals: List<Local> = emptyList(),
    body: Expression = expression(),
) = Function(
    idx = idx,
    typeIndex = typeIndex,
    locals = locals,
    body = body,
)
