package io.github.charlietap.chasm.type.rolling.substitution

import io.github.charlietap.chasm.ast.type.ResultType
import io.github.charlietap.chasm.ast.type.ValueType

internal fun ResultTypeSubstitutor(
    resultType: ResultType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
): ResultType =
    ResultTypeSubstitutor(
        resultType = resultType,
        concreteHeapTypeSubstitutor = concreteHeapTypeSubstitutor,
        valueTypeSubstitutor = ::ValueTypeSubstitutorImpl,
    )

internal fun ResultTypeSubstitutor(
    resultType: ResultType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
    valueTypeSubstitutor: TypeSubstitutor<ValueType>,
): ResultType = ResultType(
    resultType.types.map { valueType ->
        valueTypeSubstitutor(valueType, concreteHeapTypeSubstitutor)
    },
)
