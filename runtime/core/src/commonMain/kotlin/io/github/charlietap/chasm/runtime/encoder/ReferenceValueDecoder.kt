package io.github.charlietap.chasm.runtime.encoder

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.value.ReferenceValue

typealias ReferenceValueDecoder = (Long) -> ReferenceValue

inline fun ReferenceValueDecoder(
    encoded: Long,
): ReferenceValue = ReferenceValueDecoder(
    encoded = encoded,
    heapTypeDecoder = ::HeapTypeDecoder,
)

fun ReferenceValueDecoder(
    encoded: Long,
    heapTypeDecoder: HeapTypeDecoder,
): ReferenceValue {
    val typeId = encoded and RV_TYPE_MASK
    val value = encoded shr RV_SHIFT_BITS

    return when (typeId) {
        RV_TYPE_NULL -> ReferenceValue.Null(heapTypeDecoder(value.toInt()))
        RV_TYPE_I31 -> ReferenceValue.I31(value.toUInt())
        RV_TYPE_STRUCT -> ReferenceValue.Struct(Address.Struct(value.toInt()))
        RV_TYPE_ARRAY -> ReferenceValue.Array(Address.Array(value.toInt()))
        RV_TYPE_FUNCTION -> ReferenceValue.Function(Address.Function(value.toInt()))
        RV_TYPE_HOST -> ReferenceValue.Host(Address.Host(value.toInt()))
        RV_TYPE_EXCEPTION -> ReferenceValue.Exception(Address.Exception(value.toInt()))
        RV_TYPE_EXTERN -> ReferenceValue.Extern(ReferenceValueDecoder(value, heapTypeDecoder))
        else -> throw InvocationException(InvocationError.ReferenceValueExpected)
    }
}
