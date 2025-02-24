package io.github.charlietap.chasm.ast.module

import io.github.charlietap.chasm.ast.module.Index.LocalIndex
import io.github.charlietap.chasm.type.ValueType

data class Local(
    val idx: LocalIndex,
    val type: ValueType,
)
