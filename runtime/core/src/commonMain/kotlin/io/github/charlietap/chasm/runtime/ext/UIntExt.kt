package io.github.charlietap.chasm.runtime.ext

inline fun UInt.extendSigned(): Int {
    val signBit = 1 shl 30
    return if ((this and signBit.toUInt()) != 0u) {
        this.toInt() or (Int.MIN_VALUE)
    } else {
        this.toInt()
    }
}

inline fun UInt.extendUnsigned(): Int = this.toInt()
