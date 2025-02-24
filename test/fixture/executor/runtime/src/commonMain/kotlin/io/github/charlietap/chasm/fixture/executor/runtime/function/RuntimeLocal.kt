package io.github.charlietap.chasm.fixture.executor.runtime.function

import io.github.charlietap.chasm.executor.runtime.function.Local
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.fixture.executor.runtime.value.executionValue
import io.github.charlietap.chasm.fixture.type.valueType
import io.github.charlietap.chasm.ir.module.Index
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
