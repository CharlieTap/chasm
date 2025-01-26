package io.github.charlietap.chasm.executor.runtime.encoder

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

typealias ReferenceValueEncoder = (ReferenceValue) -> Long
typealias ReferenceValueDecoder = (Long) -> ReferenceValue

inline fun ReferenceValueEncoder(
    value: ReferenceValue,
): Long = ReferenceValueEncoder(
    value = value,
    heapTypeEncoder = ::HeapTypeEncoder,
)

fun ReferenceValueEncoder(
    value: ReferenceValue,
    heapTypeEncoder: HeapTypeEncoder,
): Long = when (value) {
    is ReferenceValue.Null -> {
        (heapTypeEncoder(value.heapType).toLong() shl RV_SHIFT_BITS) or RV_TYPE_NULL
    }
    is ReferenceValue.I31 -> {
        (value.value.toLong() shl RV_SHIFT_BITS) or RV_TYPE_I31
    }
    is ReferenceValue.Struct -> {
        (value.address.address.toLong() shl RV_SHIFT_BITS) or RV_TYPE_STRUCT
    }
    is ReferenceValue.Array -> {
        (value.address.address.toLong() shl RV_SHIFT_BITS) or RV_TYPE_ARRAY
    }
    is ReferenceValue.Function -> {
        (value.address.address.toLong() shl RV_SHIFT_BITS) or RV_TYPE_FUNCTION
    }
    is ReferenceValue.Host -> {
        (value.address.address.toLong() shl RV_SHIFT_BITS) or RV_TYPE_HOST
    }
    is ReferenceValue.Exception -> {
        (value.address.address.toLong() shl RV_SHIFT_BITS) or RV_TYPE_EXCEPTION
    }
    is ReferenceValue.Extern -> {
        (ReferenceValueEncoder(value.referenceValue, heapTypeEncoder) shl RV_SHIFT_BITS) or RV_TYPE_EXTERN
    }
}

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

const val RV_SHIFT_BITS = 8
const val RV_TYPE_MASK = 0xFFL

const val RV_TYPE_NULL: Long = 1
const val RV_TYPE_I31: Long = 2
const val RV_TYPE_STRUCT: Long = 3
const val RV_TYPE_ARRAY: Long = 4
const val RV_TYPE_FUNCTION: Long = 5
const val RV_TYPE_HOST: Long = 6
const val RV_TYPE_EXCEPTION: Long = 7
const val RV_TYPE_EXTERN: Long = 8
