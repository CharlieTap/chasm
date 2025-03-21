package io.github.charlietap.chasm.type.rolling.substitution

import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.ResultType

internal fun FunctionTypeSubstitutor(
    functionType: FunctionType,
    substitution: Substitution,
): FunctionType =
    FunctionTypeSubstitutor(
        functionType = functionType,
        substitution = substitution,
        resultTypeSubstitutor = ::ResultTypeSubstitutor,
    )

internal fun FunctionTypeSubstitutor(
    functionType: FunctionType,
    substitution: Substitution,
    resultTypeSubstitutor: TypeSubstitutor<ResultType>,
): FunctionType = functionType.apply {
    params = resultTypeSubstitutor(functionType.params, substitution)
    results = resultTypeSubstitutor(functionType.results, substitution)
}
