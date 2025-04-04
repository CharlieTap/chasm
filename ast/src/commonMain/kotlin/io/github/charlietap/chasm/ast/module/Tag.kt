package io.github.charlietap.chasm.ast.module

import io.github.charlietap.chasm.type.TagType

data class Tag(
    val index: Index.TagIndex,
    val type: TagType,
)
