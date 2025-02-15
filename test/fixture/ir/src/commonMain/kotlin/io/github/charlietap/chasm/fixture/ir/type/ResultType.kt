package io.github.charlietap.chasm.fixture.ir.type

import io.github.charlietap.chasm.ir.type.ResultType
import io.github.charlietap.chasm.ir.type.ValueType

fun resultType(
    types: List<ValueType> = emptyList(),
) = ResultType(
    types = types,
)
