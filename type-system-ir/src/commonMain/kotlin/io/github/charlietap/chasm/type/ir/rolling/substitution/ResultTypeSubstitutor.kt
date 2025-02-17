package io.github.charlietap.chasm.type.ir.rolling.substitution

import io.github.charlietap.chasm.ir.type.ResultType
import io.github.charlietap.chasm.ir.type.ValueType

internal fun ResultTypeSubstitutor(
    resultType: ResultType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
): ResultType =
    ResultTypeSubstitutor(
        resultType = resultType,
        concreteHeapTypeSubstitutor = concreteHeapTypeSubstitutor,
        valueTypeSubstitutor = ::ValueTypeSubstitutor,
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
