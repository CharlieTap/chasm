package io.github.charlietap.chasm.type.matching

import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.ResultType

internal fun FunctionTypeMatcher(
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
): Boolean = resultTypeMatcher(type1.params, type2.params, context) &&
    resultTypeMatcher(type1.results, type2.results, context)
