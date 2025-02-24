package io.github.charlietap.chasm.fixture.ast.module

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Tag
import io.github.charlietap.chasm.fixture.type.tagType
import io.github.charlietap.chasm.type.TagType

fun tag(
    index: Index.TagIndex = tagIndex(),
    type: TagType = tagType(),
) = Tag(
    index = index,
    type = type,
)
