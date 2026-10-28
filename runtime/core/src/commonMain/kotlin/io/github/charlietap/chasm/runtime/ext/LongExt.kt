package io.github.charlietap.chasm.runtime.ext

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.encoder.RV_SHIFT_BITS
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_EXTERN
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_FUNCTION
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_I31
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_MASK
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_NULL
import io.github.charlietap.chasm.runtime.encoder.ReferenceValueDecoder
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.runtime.value.NumberValue
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ValueType

fun Long.toReferenceValue(): ReferenceValue = ReferenceValueDecoder(this)

fun Long.toExternReference(): ReferenceValue.Extern = try {
    toReferenceValue() as ReferenceValue.Extern
} catch (_: ClassCastException) {
    throw InvocationException(InvocationError.ExternReferenceExpected)
}

inline fun Long.toFunctionAddress(): Address.Function {
    val typeId = this and RV_TYPE_MASK
    if (typeId != RV_TYPE_FUNCTION) {
        throw InvocationException(InvocationError.FunctionReferenceExpected)
    }
    val address = (this shr RV_SHIFT_BITS).toInt()
    return Address.Function(address)
}

inline fun Long.toI31(): UInt {
    val typeId = this and RV_TYPE_MASK
    if (typeId != RV_TYPE_I31) {
        throw InvocationException(InvocationError.I31ReferenceExpected)
    }
    return (this shr RV_SHIFT_BITS).toUInt()
}

inline fun Long.isNullableReference(): Boolean = (this and RV_TYPE_MASK) == RV_TYPE_NULL

inline fun Long.isExternReference(): Boolean = (this and RV_TYPE_MASK) == RV_TYPE_EXTERN

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
