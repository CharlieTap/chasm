package io.github.charlietap.chasm.fixture.module

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Tag
import io.github.charlietap.chasm.ast.type.TagType
import io.github.charlietap.chasm.fixture.type.tagType

fun tag(
    index: Index.TagIndex = tagIndex(),
    type: TagType = tagType(),
) = Tag(
    index = index,
    type = type,
)
