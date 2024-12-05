package io.github.charlietap.chasm.type.matching

import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.ast.type.VectorType

fun ValueTypeMatcher(
    type1: ValueType,
    type2: ValueType,
    context: TypeMatcherContext,
): Boolean = ValueTypeMatcher(
    type1 = type1,
    type2 = type2,
    context = context,
    numberTypeMatcher = ::NumberTypeMatcher,
    vectorTypeMatcher = ::VectorTypeMatcher,
    referenceTypeMatcher = ::ReferenceTypeMatcher,
)

internal fun ValueTypeMatcher(
    type1: ValueType,
    type2: ValueType,
    context: TypeMatcherContext,
    numberTypeMatcher: TypeMatcher<NumberType>,
    vectorTypeMatcher: TypeMatcher<VectorType>,
    referenceTypeMatcher: TypeMatcher<ReferenceType>,
): Boolean = when(type1) {
    is ValueType.Number if type2 is ValueType.Number -> numberTypeMatcher(type1.numberType, type2.numberType, context)
    is ValueType.Vector if type2 is ValueType.Vector -> vectorTypeMatcher(type1.vectorType, type2.vectorType, context)
    is ValueType.Reference if type2 is ValueType.Reference -> referenceTypeMatcher(type1.referenceType, type2.referenceType, context)
    is ValueType.Bottom -> true
    else -> false
}
