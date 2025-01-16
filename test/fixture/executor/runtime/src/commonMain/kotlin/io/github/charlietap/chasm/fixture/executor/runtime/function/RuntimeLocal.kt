package io.github.charlietap.chasm.fixture.executor.runtime.function

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.executor.runtime.function.Local
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.fixture.ast.type.valueType
import io.github.charlietap.chasm.fixture.executor.runtime.value.executionValue

fun runtimeLocal(
    idx: Index.LocalIndex = Index.LocalIndex(0u),
    type: ValueType = valueType(),
    default: ExecutionValue = executionValue(),
) = Local(
    idx = idx,
    type = type,
    default = default,
)
