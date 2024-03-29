package io.github.charlietap.chasm.ast.module

import io.github.charlietap.chasm.ast.instruction.Index.TypeIndex
import io.github.charlietap.chasm.ast.type.FunctionType

data class Type(
    val idx: TypeIndex,
    val functionType: FunctionType,
)
