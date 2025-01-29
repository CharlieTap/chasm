package io.github.charlietap.chasm.executor.runtime.encoder

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

fun ReferenceValue.toLong(): Long = ReferenceValueEncoder(this)

fun Long.toReferenceValue(): ReferenceValue = ReferenceValueDecoder(this)

fun Long.toExternReference(): ReferenceValue.Extern = try {
    this.toReferenceValue() as ReferenceValue.Extern
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

inline fun Long.isNullableReference(): Boolean = (this and RV_TYPE_MASK) == RV_TYPE_NULL

inline fun Long.isExternReference(): Boolean = (this and RV_TYPE_MASK) == RV_TYPE_EXTERN

inline fun Long.isI31Reference(): Boolean = (this and RV_TYPE_MASK) == RV_TYPE_I31

inline fun Long.isStructReference(): Boolean = (this and RV_TYPE_MASK) == RV_TYPE_STRUCT

inline fun Long.isArrayReference(): Boolean = (this and RV_TYPE_MASK) == RV_TYPE_ARRAY

inline fun Long.isFunctionReference(): Boolean = (this and RV_TYPE_MASK) == RV_TYPE_FUNCTION

inline fun Long.isHostReference(): Boolean = (this and RV_TYPE_MASK) == RV_TYPE_HOST

inline fun Long.isExceptionReference(): Boolean = (this and RV_TYPE_MASK) == RV_TYPE_EXCEPTION
