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

inline fun LongArray.orInto(
    into: LongArray,
    words: Int,
    offset: Int = 0,
    intoOffset: Int = 0,
) {
    for (word in 0 until words) into[intoOffset + word] = into[intoOffset + word] or this[offset + word]
}
