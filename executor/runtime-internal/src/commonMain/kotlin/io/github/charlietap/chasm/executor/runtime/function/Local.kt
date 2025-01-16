package io.github.charlietap.chasm.executor.runtime.function

import io.github.charlietap.chasm.ast.module.Index.LocalIndex
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

data class Local(
    val idx: LocalIndex,
    val type: ValueType,
    val default: ExecutionValue,
)
