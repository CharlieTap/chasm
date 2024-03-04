package io.github.charlietap.chasm.ast.module

import io.github.charlietap.chasm.ast.instruction.Index.LocalIndex
import io.github.charlietap.chasm.ast.type.ValueType

data class Local(
    val idx: LocalIndex,
    val type: ValueType,
)
