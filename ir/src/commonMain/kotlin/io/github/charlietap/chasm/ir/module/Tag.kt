package io.github.charlietap.chasm.ir.module

import io.github.charlietap.chasm.ir.type.TagType

data class Tag(
    val index: Index.TagIndex,
    val type: TagType,
)
