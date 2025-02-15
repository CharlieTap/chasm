package io.github.charlietap.chasm.ir.module

import io.github.charlietap.chasm.ir.instruction.Expression

data class Function(
    val idx: Index.FunctionIndex,
    val typeIndex: Index.TypeIndex,
    val locals: List<Local>,
    val body: Expression,
)
