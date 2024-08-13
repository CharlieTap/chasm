package io.github.charlietap.chasm.embedding.fixture

import io.github.charlietap.chasm.embedding.shapes.GlobalType
import io.github.charlietap.chasm.embedding.shapes.Mutability
import io.github.charlietap.chasm.embedding.shapes.ValueType

fun publicGlobalType(
    valueType: ValueType = publicValueType(),
    mutability: Mutability = Mutability.Variable,
) = GlobalType(valueType, mutability)
