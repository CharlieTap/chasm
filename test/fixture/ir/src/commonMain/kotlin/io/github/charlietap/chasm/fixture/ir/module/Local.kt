package io.github.charlietap.chasm.fixture.ir.module

import io.github.charlietap.chasm.fixture.type.valueType
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.ir.module.Local
import io.github.charlietap.chasm.type.ValueType

fun local(
    idx: Index.LocalIndex = localIndex(),
    type: ValueType = valueType(),
) = Local(
    idx = idx,
    type = type,
)
