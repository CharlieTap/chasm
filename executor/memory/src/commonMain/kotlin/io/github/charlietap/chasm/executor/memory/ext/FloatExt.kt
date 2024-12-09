@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.memory.ext

inline fun Float.copyInto(buffer: ByteArray, offset: Int) {
    val bits = this.toRawBits()
    buffer[offset] = (bits and 0xFF).toByte()
    buffer[offset + 1] = (bits shr 8 and 0xFF).toByte()
    buffer[offset + 2] = (bits shr 16 and 0xFF).toByte()
    buffer[offset + 3] = (bits shr 24 and 0xFF).toByte()
}
