package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.TagType

fun tagType(
    attribute: TagType.Attribute = attribute(),
    typeIndex: Int = 0,
    functionType: FunctionType = functionType(),
) = TagType(
    attribute = attribute,
    typeIndex = typeIndex,
    functionType = functionType,
)
