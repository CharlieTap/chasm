package io.github.charlietap.chasm.compiler.backend.analysis

inline fun LongArray.setBit(bit: Int) {
    this[bit ushr 6] = this[bit ushr 6] or (1L shl (bit and 63))
}

inline fun LongArray.clearBit(bit: Int) {
    this[bit ushr 6] = this[bit ushr 6] and (1L shl (bit and 63)).inv()
}

inline fun LongArray.hasBit(bit: Int): Boolean {
    return this[bit ushr 6] and (1L shl (bit and 63)) != 0L
}
