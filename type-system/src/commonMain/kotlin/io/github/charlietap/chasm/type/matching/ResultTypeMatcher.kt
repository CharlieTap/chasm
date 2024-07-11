package io.github.charlietap.chasm.type.matching

import io.github.charlietap.chasm.ast.type.ResultType
import io.github.charlietap.chasm.ast.type.ValueType

internal fun ResultTypeMatcher(
    type1: ResultType,
    type2: ResultType,
    context: TypeMatcherContext,
): Boolean = ResultTypeMatcher(
    type1 = type1,
    type2 = type2,
    context = context,
    valueTypeMatcher = ::ValueTypeMatcher,
)

internal fun ResultTypeMatcher(
    type1: ResultType,
    type2: ResultType,
    context: TypeMatcherContext,
    valueTypeMatcher: TypeMatcher<ValueType>,
): Boolean = when {
    type1.types.size != type2.types.size -> false
    else -> type1.types.zip(type2.types).all { (valueType1, valueType2) ->
        valueTypeMatcher(valueType1, valueType2, context)
    }
}
