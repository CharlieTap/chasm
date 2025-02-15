package io.github.charlietap.chasm.fixture.ir.type

import io.github.charlietap.chasm.ir.type.GlobalType
import io.github.charlietap.chasm.ir.type.Mutability
import io.github.charlietap.chasm.ir.type.ValueType

fun globalType(
    valueType: ValueType = valueType(),
    mutability: Mutability = mutability(),
) = GlobalType(
    valueType = valueType,
    mutability = mutability,
)
