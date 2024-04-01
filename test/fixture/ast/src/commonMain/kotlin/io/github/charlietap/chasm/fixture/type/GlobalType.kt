package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.ast.type.Mutability
import io.github.charlietap.chasm.ast.type.ValueType

fun globalType(
    valueType: ValueType = valueType(),
    mutability: Mutability = mutability(),
) = GlobalType(
    valueType = valueType,
    mutability = mutability,
)
