package io.github.charlietap.chasm.type.rolling.substitution

import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.TagType

fun TagTypeSubstitutor(
    tagType: TagType,
    substitution: Substitution,
): TagType =
    TagTypeSubstitutor(
        tagType = tagType,
        substitution = substitution,
        functionTypeSubstitutor = ::FunctionTypeSubstitutor,
    )

internal fun TagTypeSubstitutor(
    tagType: TagType,
    substitution: Substitution,
    functionTypeSubstitutor: TypeSubstitutor<FunctionType>,
): TagType = tagType.apply {
    functionType = functionTypeSubstitutor(tagType.functionType, substitution)
}
