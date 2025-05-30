package io.github.charlietap.chasm.runtime.ext

import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.runtime.value.NumberValue
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.runtime.value.VectorValue

inline fun ExecutionValue.toLongFromBoxed(): Long = when (this) {
    is ExecutionValue.Uninitialised -> 0L
    is NumberValue.I32 -> value.toLong()
    is NumberValue.I64 -> value
    is NumberValue.F32 -> value.toRawBits().toLong()
    is NumberValue.F64 -> value.toRawBits()
    is ReferenceValue -> this.toLongFromBoxed()
    is VectorValue.V128 -> TODO()
}
