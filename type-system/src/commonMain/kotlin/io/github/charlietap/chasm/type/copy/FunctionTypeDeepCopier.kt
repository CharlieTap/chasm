package io.github.charlietap.chasm.type.copy

import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.ResultType

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
