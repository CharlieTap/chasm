package io.github.charlietap.chasm.executor.type.rolling.substitution

import io.github.charlietap.chasm.ast.type.ResultType
import io.github.charlietap.chasm.ast.type.ValueType

internal fun ResultTypeSubstitutorImpl(
    resultType: ResultType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
): ResultType =
    ResultTypeSubstitutorImpl(
        resultType = resultType,
        concreteHeapTypeSubstitutor = concreteHeapTypeSubstitutor,
        valueTypeSubstitutor = ::ValueTypeSubstitutorImpl,
    )

internal fun ResultTypeSubstitutorImpl(
    resultType: ResultType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
    valueTypeSubstitutor: TypeSubstitutor<ValueType>,
): ResultType = ResultType(
    resultType.types.map { valueType ->
        valueTypeSubstitutor(valueType, concreteHeapTypeSubstitutor)
    },
)
