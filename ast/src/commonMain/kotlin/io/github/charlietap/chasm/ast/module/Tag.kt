package io.github.charlietap.chasm.ast.module

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.type.TagType

@InternalChasmApi
data class Tag(
    val index: Index.TagIndex,
    val type: TagType,
)
