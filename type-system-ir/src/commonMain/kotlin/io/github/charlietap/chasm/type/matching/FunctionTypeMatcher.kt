package io.github.charlietap.chasm.type.matching

import io.github.charlietap.chasm.ir.type.FunctionType
import io.github.charlietap.chasm.ir.type.ResultType

fun FunctionTypeMatcher(
    type1: FunctionType,
    type2: FunctionType,
    context: TypeMatcherContext,
): Boolean = FunctionTypeMatcher(
    type1 = type1,
    type2 = type2,
    context = context,
    resultTypeMatcher = ::ResultTypeMatcher,
)

internal fun FunctionTypeMatcher(
    type1: FunctionType,
    type2: FunctionType,
    context: TypeMatcherContext,
    resultTypeMatcher: TypeMatcher<ResultType>,
): Boolean = resultTypeMatcher(type2.params, type1.params, context) &&
    resultTypeMatcher(type1.results, type2.results, context)
