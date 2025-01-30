package io.github.charlietap.chasm.executor.runtime.ext

import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.executor.runtime.encoder.RV_SHIFT_BITS
import io.github.charlietap.chasm.executor.runtime.encoder.RV_TYPE_ARRAY
import io.github.charlietap.chasm.executor.runtime.encoder.RV_TYPE_EXCEPTION
import io.github.charlietap.chasm.executor.runtime.encoder.RV_TYPE_EXTERN
import io.github.charlietap.chasm.executor.runtime.encoder.RV_TYPE_FUNCTION
import io.github.charlietap.chasm.executor.runtime.encoder.RV_TYPE_HOST
import io.github.charlietap.chasm.executor.runtime.encoder.RV_TYPE_I31
import io.github.charlietap.chasm.executor.runtime.encoder.RV_TYPE_MASK
import io.github.charlietap.chasm.executor.runtime.encoder.RV_TYPE_NULL
import io.github.charlietap.chasm.executor.runtime.encoder.RV_TYPE_STRUCT
import io.github.charlietap.chasm.executor.runtime.encoder.ReferenceValueDecoder
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

fun Long.toReferenceValue(): ReferenceValue = ReferenceValueDecoder(this)

fun Long.toExternReference(): ReferenceValue.Extern = try {
    toReferenceValue() as ReferenceValue.Extern
} catch (_: ClassCastException) {
    throw InvocationException(InvocationError.ExternReferenceExpected)
}

fun Long.toNullableReference(): ReferenceValue.Null = try {
    toReferenceValue() as ReferenceValue.Null
} catch (_: ClassCastException) {
    throw InvocationException(InvocationError.NullReferenceExpected)
}

inline fun Long.toArrayAddress(): Address.Array {
    val typeId = this and RV_TYPE_MASK
    if (typeId != RV_TYPE_ARRAY) {
        throw InvocationException(InvocationError.ArrayReferenceExpected)
    }
    val address = (this shr RV_SHIFT_BITS).toInt()
    return Address.Array(address)
}

inline fun Long.toFunctionAddress(): Address.Function {
    val typeId = this and RV_TYPE_MASK
    if (typeId != RV_TYPE_FUNCTION) {
        throw InvocationException(InvocationError.FunctionReferenceExpected)
    }
    val address = (this shr RV_SHIFT_BITS).toInt()
    return Address.Function(address)
}

inline fun Long.toStructAddress(): Address.Struct {
    val typeId = this and RV_TYPE_MASK
    if (typeId != RV_TYPE_STRUCT) {
        throw InvocationException(InvocationError.StructReferenceExpected)
    }
    val address = (this shr RV_SHIFT_BITS).toInt()
    return Address.Struct(address)
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

inline fun Long.isI31Reference(): Boolean = (this and RV_TYPE_MASK) == RV_TYPE_I31

inline fun Long.isStructReference(): Boolean = (this and RV_TYPE_MASK) == RV_TYPE_STRUCT

inline fun Long.isArrayReference(): Boolean = (this and RV_TYPE_MASK) == RV_TYPE_ARRAY

inline fun Long.isFunctionReference(): Boolean = (this and RV_TYPE_MASK) == RV_TYPE_FUNCTION

inline fun Long.isHostReference(): Boolean = (this and RV_TYPE_MASK) == RV_TYPE_HOST

inline fun Long.isExceptionReference(): Boolean = (this and RV_TYPE_MASK) == RV_TYPE_EXCEPTION

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
