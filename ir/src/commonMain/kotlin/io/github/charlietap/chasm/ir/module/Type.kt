package io.github.charlietap.chasm.ir.module

import io.github.charlietap.chasm.type.RecursiveType

data class Type(
    val idx: Index.TypeIndex,
    val recursiveType: RecursiveType,
)
