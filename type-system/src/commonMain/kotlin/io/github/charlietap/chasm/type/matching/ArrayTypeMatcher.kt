package io.github.charlietap.chasm.type.matching

import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.FieldType

internal fun ArrayTypeMatcher(
    type1: ArrayType,
    type2: ArrayType,
    context: TypeMatcherContext,
): Boolean = ArrayTypeMatcher(
    type1 = type1,
    type2 = type2,
    context = context,
    fieldTypeMatcher = ::FieldTypeMatcher,
)

internal fun ArrayTypeMatcher(
    type1: ArrayType,
    type2: ArrayType,
    context: TypeMatcherContext,
    fieldTypeMatcher: TypeMatcher<FieldType>,
): Boolean = fieldTypeMatcher(type1.fieldType, type2.fieldType, context)
