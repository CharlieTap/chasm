package io.github.charlietap.chasm.ast.module

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.ast.module.Index.TypeIndex
import io.github.charlietap.chasm.type.RecursiveType

@InternalChasmApi
data class Type(
    val idx: TypeIndex,
    val recursiveType: RecursiveType,
)
