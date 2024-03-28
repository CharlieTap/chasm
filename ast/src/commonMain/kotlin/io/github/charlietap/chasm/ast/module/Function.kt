package io.github.charlietap.chasm.ast.module

import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Index.FunctionIndex
import io.github.charlietap.chasm.ast.module.Index.TypeIndex

data class Function(
    val idx: FunctionIndex,
    val typeIndex: TypeIndex,
    val locals: List<Local>,
    val body: Expression,
)
