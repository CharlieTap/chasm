package io.github.charlietap.chasm.executor.runtime.function

import io.github.charlietap.chasm.ast.module.Index.FunctionIndex
import io.github.charlietap.chasm.ast.module.Index.TypeIndex
import io.github.charlietap.chasm.ast.module.Local

data class Function(
    val idx: FunctionIndex,
    val typeIndex: TypeIndex,
    val locals: List<Local>,
    val body: Expression,
)
