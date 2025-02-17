package io.github.charlietap.chasm.type.ir.copy

import io.github.charlietap.chasm.ir.type.FunctionType
import io.github.charlietap.chasm.ir.type.ResultType

fun FunctionTypeDeepCopier(
    input: FunctionType,
): FunctionType =
    FunctionTypeDeepCopier(
        input = input,
        resultTypeCopier = ::ResultTypeDeepCopier,
    )

inline fun FunctionTypeDeepCopier(
    input: FunctionType,
    resultTypeCopier: DeepCopier<ResultType>,
): FunctionType = input.copy(
    params = resultTypeCopier(input.params),
    results = resultTypeCopier(input.results),
)
