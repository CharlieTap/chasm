package io.github.charlietap.chasm.fixture.ast.module

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Local
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.fixture.ast.type.valueType

fun local(
    idx: Index.LocalIndex = Index.LocalIndex(0u),
    type: ValueType = valueType(),
) = Local(
    idx = idx,
    type = type,
)
