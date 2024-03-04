package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.ast.type.ValueType

fun globalType(
    valueType: ValueType = valueType(),
    mutability: GlobalType.Mutability = GlobalType.Mutability.Var,
) = GlobalType(
    valueType = valueType,
    mutability = mutability,
)
