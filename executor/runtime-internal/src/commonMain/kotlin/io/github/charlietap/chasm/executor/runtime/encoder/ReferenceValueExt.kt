package io.github.charlietap.chasm.executor.runtime.encoder

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

fun ReferenceValue.toLong(): Long = ReferenceValueEncoder(this)

fun Long.toReferenceValue(): ReferenceValue = ReferenceValueDecoder(this)

inline fun Long.toFunctionAddress(): Address.Function {
    val typeId = this and RV_TYPE_MASK
    if (typeId != RV_TYPE_FUNCTION) {
        throw InvocationException(InvocationError.FunctionReferenceExpected)
    }
    val address = (this shr RV_SHIFT_BITS).toInt()
    return Address.Function(address)
}

inline fun Long.isNullableReference(): Boolean = (this and RV_TYPE_MASK) == RV_TYPE_NULL
