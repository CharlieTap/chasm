package io.github.charlietap.chasm.fixture.runtime.function

import io.github.charlietap.chasm.fixture.runtime.value.executionValue
import io.github.charlietap.chasm.fixture.type.valueType
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.runtime.function.Local
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.type.ValueType

fun runtimeLocal(
    idx: Index.LocalIndex = Index.LocalIndex(0),
    type: ValueType = valueType(),
    default: ExecutionValue = executionValue(),
) = Local(
    idx = idx,
    type = type,
    default = default,
)
