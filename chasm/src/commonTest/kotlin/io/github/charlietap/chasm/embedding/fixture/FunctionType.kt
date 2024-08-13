package io.github.charlietap.chasm.embedding.fixture

import io.github.charlietap.chasm.embedding.shapes.FunctionType
import io.github.charlietap.chasm.embedding.shapes.ValueType

fun publicFunctionType(
    params: List<ValueType> = emptyList(),
    results: List<ValueType> = emptyList(),
) = FunctionType(params, results)
