package io.github.charlietap.chasm.type.rolling.substitution

import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.ResultType

internal fun FunctionTypeSubstitutor(
    functionType: FunctionType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
): FunctionType =
    FunctionTypeSubstitutor(
        functionType = functionType,
        concreteHeapTypeSubstitutor = concreteHeapTypeSubstitutor,
        resultTypeSubstitutor = ::ResultTypeSubstitutor,
    )

internal fun FunctionTypeSubstitutor(
    functionType: FunctionType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
    resultTypeSubstitutor: TypeSubstitutor<ResultType>,
): FunctionType = functionType.apply {
    params = resultTypeSubstitutor(functionType.params, concreteHeapTypeSubstitutor)
    results = resultTypeSubstitutor(functionType.results, concreteHeapTypeSubstitutor)
}
