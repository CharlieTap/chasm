package io.github.charlietap.chasm.ir.module

import io.github.charlietap.chasm.ir.type.RecursiveType

data class Type(
    val idx: Index.TypeIndex,
    val recursiveType: RecursiveType,
)
