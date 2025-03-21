package io.github.charlietap.chasm.type.rolling.substitution

import io.github.charlietap.chasm.type.ResultType
import io.github.charlietap.chasm.type.ValueType

internal fun ResultTypeSubstitutor(
    resultType: ResultType,
    substitution: Substitution,
): ResultType =
    ResultTypeSubstitutor(
        resultType = resultType,
        substitution = substitution,
        valueTypeSubstitutor = ::ValueTypeSubstitutor,
    )

internal fun ResultTypeSubstitutor(
    resultType: ResultType,
    substitution: Substitution,
    valueTypeSubstitutor: TypeSubstitutor<ValueType>,
): ResultType = ResultType(
    resultType.types.map { valueType ->
        valueTypeSubstitutor(valueType, substitution)
    },
)
