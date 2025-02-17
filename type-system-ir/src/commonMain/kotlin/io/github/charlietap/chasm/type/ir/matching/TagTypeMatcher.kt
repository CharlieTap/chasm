package io.github.charlietap.chasm.type.ir.matching

import io.github.charlietap.chasm.ir.type.FunctionType
import io.github.charlietap.chasm.ir.type.TagType

fun TagTypeMatcher(
    type1: TagType,
    type2: TagType,
    context: TypeMatcherContext,
): Boolean = TagTypeMatcher(
    type1 = type1,
    type2 = type2,
    context = context,
    functionTypeMatcher = ::FunctionTypeMatcher,
)

internal fun TagTypeMatcher(
    type1: TagType,
    type2: TagType,
    context: TypeMatcherContext,
    functionTypeMatcher: TypeMatcher<FunctionType>,
): Boolean = functionTypeMatcher(type1.type, type2.type, context)
