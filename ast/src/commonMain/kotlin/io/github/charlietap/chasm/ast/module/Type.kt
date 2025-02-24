package io.github.charlietap.chasm.ast.module

import io.github.charlietap.chasm.ast.module.Index.TypeIndex
import io.github.charlietap.chasm.type.RecursiveType

data class Type(
    val idx: TypeIndex,
    val recursiveType: RecursiveType,
)
