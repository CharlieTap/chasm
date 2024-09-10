package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.TagType

fun tagType(
    attribute: TagType.Attribute = attribute(),
    type: FunctionType = functionType(),
) = TagType(
    attribute = attribute,
    type = type,
)
