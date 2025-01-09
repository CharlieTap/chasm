package io.github.charlietap.chasm.executor.runtime.ext

import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

inline fun ReferenceValue.I31.extendSigned(): Int {
    val signBit = 1 shl 30
    return if ((value and signBit.toUInt()) != 0u) {
        value.toInt() or (Int.MIN_VALUE)
    } else {
        value.toInt()
    }
}

inline fun ReferenceValue.I31.extendUnsigned(): Int = value.toInt()
