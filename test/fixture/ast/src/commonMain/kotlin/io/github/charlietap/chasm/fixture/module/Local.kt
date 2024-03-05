package io.github.charlietap.chasm.fixture.module

import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.module.Local
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.fixture.type.valueType

fun local(
    idx: Index.LocalIndex = Index.LocalIndex(0u),
    type: ValueType = valueType(),
) = Local(
    idx = idx,
    type = type,
)
