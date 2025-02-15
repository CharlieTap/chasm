package io.github.charlietap.chasm.fixture.ir.type

import io.github.charlietap.chasm.ir.type.FunctionType
import io.github.charlietap.chasm.ir.type.TagType

fun tagType(
    attribute: TagType.Attribute = attribute(),
    type: FunctionType = functionType(),
) = TagType(
    attribute = attribute,
    type = type,
)
