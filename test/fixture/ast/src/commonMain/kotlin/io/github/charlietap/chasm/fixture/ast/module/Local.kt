package io.github.charlietap.chasm.fixture.ast.module

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Local
import io.github.charlietap.chasm.fixture.type.valueType
import io.github.charlietap.chasm.type.ValueType

fun local(
    idx: Index.LocalIndex = Index.LocalIndex(0u),
    type: ValueType = valueType(),
) = Local(
    idx = idx,
    type = type,
)
