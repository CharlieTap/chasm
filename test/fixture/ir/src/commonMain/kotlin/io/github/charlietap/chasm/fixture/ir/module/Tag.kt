package io.github.charlietap.chasm.fixture.ir.module

import io.github.charlietap.chasm.fixture.type.tagType
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.ir.module.Tag
import io.github.charlietap.chasm.type.TagType

fun tag(
    index: Index.TagIndex = tagIndex(),
    type: TagType = tagType(),
) = Tag(
    index = index,
    type = type,
)
