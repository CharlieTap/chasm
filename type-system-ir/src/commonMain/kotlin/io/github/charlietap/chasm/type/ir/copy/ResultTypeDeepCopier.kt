package io.github.charlietap.chasm.type.ir.copy

import io.github.charlietap.chasm.ir.type.ResultType
import io.github.charlietap.chasm.ir.type.ValueType

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
