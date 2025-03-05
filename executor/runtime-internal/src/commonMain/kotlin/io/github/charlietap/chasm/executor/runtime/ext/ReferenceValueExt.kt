package io.github.charlietap.chasm.executor.runtime.ext

import io.github.charlietap.chasm.executor.runtime.encoder.HeapTypeEncoder
import io.github.charlietap.chasm.executor.runtime.encoder.RV_SHIFT_BITS
import io.github.charlietap.chasm.executor.runtime.encoder.RV_TYPE_ARRAY
import io.github.charlietap.chasm.executor.runtime.encoder.RV_TYPE_EXCEPTION
import io.github.charlietap.chasm.executor.runtime.encoder.RV_TYPE_FUNCTION
import io.github.charlietap.chasm.executor.runtime.encoder.RV_TYPE_HOST
import io.github.charlietap.chasm.executor.runtime.encoder.RV_TYPE_I31
import io.github.charlietap.chasm.executor.runtime.encoder.RV_TYPE_NULL
import io.github.charlietap.chasm.executor.runtime.encoder.RV_TYPE_STRUCT
import io.github.charlietap.chasm.executor.runtime.encoder.ReferenceValueEncoder
import io.github.charlietap.chasm.runtime.value.ReferenceValue

inline fun ReferenceValue.toLongFromBoxed(): Long = ReferenceValueEncoder(this)

inline fun ReferenceValue.Null.toLong(
    heapTypeEncoder: HeapTypeEncoder = ::HeapTypeEncoder,
): Long {
    return (heapTypeEncoder(this.heapType).toLong() shl RV_SHIFT_BITS) or RV_TYPE_NULL
}

inline fun ReferenceValue.I31.toLong(): Long {
    return (this.value.toLong() shl RV_SHIFT_BITS) or RV_TYPE_I31
}

inline fun ReferenceValue.Struct.toLong(): Long {
    return (this.address.address.toLong() shl RV_SHIFT_BITS) or RV_TYPE_STRUCT
}

inline fun ReferenceValue.Array.toLong(): Long {
    return (this.address.address.toLong() shl RV_SHIFT_BITS) or RV_TYPE_ARRAY
}

inline fun ReferenceValue.Function.toLong(): Long {
    return (this.address.address.toLong() shl RV_SHIFT_BITS) or RV_TYPE_FUNCTION
}

inline fun ReferenceValue.Host.toLong(): Long {
    return (this.address.address.toLong() shl RV_SHIFT_BITS) or RV_TYPE_HOST
}

inline fun ReferenceValue.Exception.toLong(): Long {
    return (this.address.address.toLong() shl RV_SHIFT_BITS) or RV_TYPE_EXCEPTION
}
