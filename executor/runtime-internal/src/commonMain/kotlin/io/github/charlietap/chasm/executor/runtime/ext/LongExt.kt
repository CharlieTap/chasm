package io.github.charlietap.chasm.executor.runtime.ext

import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.executor.runtime.encoder.toReferenceValue
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.executor.runtime.value.NumberValue

inline fun Long.toExecutionValue(
    type: ValueType,
): ExecutionValue = when (type) {
    is ValueType.Number -> when (type.numberType) {
        NumberType.I32 -> NumberValue.I32(this.toInt())
        NumberType.I64 -> NumberValue.I64(this)
        NumberType.F32 -> NumberValue.F32(Float.fromBits(this.toInt()))
        NumberType.F64 -> NumberValue.F64(Double.fromBits(this))
    }
    is ValueType.Reference -> this.toReferenceValue()
    is ValueType.Bottom,
    is ValueType.Vector,
    -> TODO()
}
