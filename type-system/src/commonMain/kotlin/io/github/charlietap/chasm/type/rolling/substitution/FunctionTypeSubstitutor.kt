package io.github.charlietap.chasm.type.rolling.substitution

import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.ResultType

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
): FunctionType {
    val params = resultTypeSubstitutor(functionType.params, concreteHeapTypeSubstitutor)
    val results = resultTypeSubstitutor(functionType.results, concreteHeapTypeSubstitutor)
    return FunctionType(params, results)
}
