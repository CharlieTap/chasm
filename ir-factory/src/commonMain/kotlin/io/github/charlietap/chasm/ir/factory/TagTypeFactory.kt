package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.TagType
import io.github.charlietap.chasm.ir.type.FunctionType as IRFunctionType
import io.github.charlietap.chasm.ir.type.TagType as IRTagType

internal fun TagTypeFactory(
    tagType: TagType,
): IRTagType = TagTypeFactory(
    tagType = tagType,
    functionTypeFactory = ::FunctionTypeFactory,
)

internal inline fun TagTypeFactory(
    tagType: TagType,
    functionTypeFactory: IRFactory<FunctionType, IRFunctionType>,
): IRTagType {
    return IRTagType(
        attribute = TagTypeAttributeFactory(tagType.attribute),
        type = functionTypeFactory(tagType.type),
    )
}

internal fun TagTypeAttributeFactory(
    attribute: TagType.Attribute,
): IRTagType.Attribute {
    return when (attribute) {
        TagType.Attribute.Exception -> IRTagType.Attribute.Exception
    }
}
