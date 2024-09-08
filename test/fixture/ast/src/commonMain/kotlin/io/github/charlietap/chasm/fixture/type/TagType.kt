package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.TagType
import io.github.charlietap.chasm.fixture.module.typeIndex

fun tagType(
    attribute: TagType.Attribute = attribute(),
    typeIndex: Index.TypeIndex = typeIndex(),
) = TagType(
    attribute = attribute,
    index = typeIndex,
)
