package io.github.charlietap.chasm.executor.runtime.ext

import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.executor.runtime.value.VectorValue

inline fun ExecutionValue.toLong(): Long = when (this) {
    is ExecutionValue.Uninitialised -> 0L
    is NumberValue.I32 -> value.toLong()
    is NumberValue.I64 -> value
    is NumberValue.F32 -> value.toRawBits().toLong()
    is NumberValue.F64 -> value.toRawBits()
    is ReferenceValue -> this.toLong()
    is VectorValue.V128 -> TODO()
}
