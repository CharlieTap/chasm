package io.github.charlietap.chasm.fixture.ir.type

import io.github.charlietap.chasm.ir.type.FunctionType
import io.github.charlietap.chasm.ir.type.ResultType

fun functionType(
    params: ResultType = ResultType(emptyList()),
    results: ResultType = ResultType(emptyList()),
) = FunctionType(
    params,
    results,
)
