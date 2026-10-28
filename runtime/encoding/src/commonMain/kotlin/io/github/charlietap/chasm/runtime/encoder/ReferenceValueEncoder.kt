package io.github.charlietap.chasm.runtime.encoder

import io.github.charlietap.chasm.runtime.value.ReferenceValue

typealias ReferenceValueEncoder = (ReferenceValue) -> Long

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
