package io.github.charlietap.chasm.ir.module

import io.github.charlietap.chasm.type.ValueType

data class Local(
    val idx: Index.LocalIndex,
    val type: ValueType,
)
