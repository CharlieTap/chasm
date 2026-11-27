package io.github.charlietap.chasm.runtime.ext

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.encoder.HeapTypeEncoder
import io.github.charlietap.chasm.runtime.encoder.RV_SHIFT_BITS
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_ARRAY
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_EXCEPTION
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_FUNCTION
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_HOST
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_I31
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_NULL
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_STRUCT
import io.github.charlietap.chasm.runtime.encoder.ReferenceValueEncoder
import io.github.charlietap.chasm.runtime.value.ReferenceValue

@InternalChasmApi
inline fun ReferenceValue.toLongFromBoxed(): Long = ReferenceValueEncoder(this)

@InternalChasmApi
inline fun ReferenceValue.Null.toLong(
    heapTypeEncoder: HeapTypeEncoder = ::HeapTypeEncoder,
): Long {
    return (heapTypeEncoder(this.heapType).toLong() shl RV_SHIFT_BITS) or RV_TYPE_NULL
}

@InternalChasmApi
inline fun ReferenceValue.I31.toLong(): Long {
    return (this.value.toLong() shl RV_SHIFT_BITS) or RV_TYPE_I31
}

@InternalChasmApi
inline fun ReferenceValue.Struct.toLong(): Long {
    return (this.address.address.toLong() shl RV_SHIFT_BITS) or RV_TYPE_STRUCT
}

@InternalChasmApi
inline fun ReferenceValue.Array.toLong(): Long {
    return (this.address.address.toLong() shl RV_SHIFT_BITS) or RV_TYPE_ARRAY
}

@InternalChasmApi
inline fun ReferenceValue.Function.toLong(): Long {
    return (this.address.address.toLong() shl RV_SHIFT_BITS) or RV_TYPE_FUNCTION
}

@InternalChasmApi
inline fun ReferenceValue.Host.toLong(): Long {
    return (this.address.address.toLong() shl RV_SHIFT_BITS) or RV_TYPE_HOST
}

@InternalChasmApi
inline fun ReferenceValue.Exception.toLong(): Long {
    return (this.address.address.toLong() shl RV_SHIFT_BITS) or RV_TYPE_EXCEPTION
}
