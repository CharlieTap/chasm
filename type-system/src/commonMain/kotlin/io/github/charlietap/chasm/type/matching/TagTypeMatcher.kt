package io.github.charlietap.chasm.type.matching

import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.TagType

fun TagTypeMatcher(
    type1: TagType,
    type2: TagType,
    context: TypeMatcherContext,
): Boolean = TagTypeMatcher(
    type1 = type1,
    type2 = type2,
    context = context,
    definedTypeMatcher = ::DefinedTypeMatcher,
)

internal fun TagTypeMatcher(
    type1: TagType,
    type2: TagType,
    context: TypeMatcherContext,
    definedTypeMatcher: TypeMatcher<DefinedType>,
): Boolean = definedTypeMatcher(type1.definedType, type2.definedType, context)
