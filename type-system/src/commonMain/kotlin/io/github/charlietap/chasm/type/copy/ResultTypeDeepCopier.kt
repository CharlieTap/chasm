package io.github.charlietap.chasm.type.copy

import io.github.charlietap.chasm.type.ResultType
import io.github.charlietap.chasm.type.ValueType

fun ResultTypeDeepCopier(
    input: ResultType,
): ResultType =
    ResultTypeDeepCopier(
        input = input,
        valueTypeCopier = ::ValueTypeDeepCopier,
    )

inline fun ResultTypeDeepCopier(
    input: ResultType,
    valueTypeCopier: DeepCopier<ValueType>,
): ResultType = ResultType(
    types = input.types.map(valueTypeCopier),
)
