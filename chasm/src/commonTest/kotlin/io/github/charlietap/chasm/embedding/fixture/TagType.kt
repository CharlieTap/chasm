package io.github.charlietap.chasm.embedding.fixture

import io.github.charlietap.chasm.embedding.shapes.FunctionType
import io.github.charlietap.chasm.embedding.shapes.TagType

fun publicTagType(
    attribute: TagType.Attribute = TagType.Attribute.Exception,
    type: FunctionType = publicFunctionType(),
) = TagType(
    attribute = attribute,
    type = type,
)
