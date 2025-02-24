package io.github.charlietap.chasm.executor.runtime.function

import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.type.ValueType

data class Local(
    val idx: Index.LocalIndex,
    val type: ValueType,
    val default: ExecutionValue,
)
