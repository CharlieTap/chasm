package io.github.charlietap.chasm.runtime.function

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.type.ValueType

@InternalChasmApi
data class Local(
    val idx: Index.LocalIndex,
    val type: ValueType,
    val default: ExecutionValue,
)
