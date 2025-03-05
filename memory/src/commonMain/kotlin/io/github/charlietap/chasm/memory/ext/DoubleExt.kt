package io.github.charlietap.chasm.memory.ext

inline fun Double.copyInto(buffer: ByteArray, offset: Int) {
    val bits = this.toRawBits()
    buffer[offset] = (bits and 0xFF).toByte()
    buffer[offset + 1] = (bits shr 8 and 0xFF).toByte()
    buffer[offset + 2] = (bits shr 16 and 0xFF).toByte()
    buffer[offset + 3] = (bits shr 24 and 0xFF).toByte()
    buffer[offset + 4] = (bits shr 32 and 0xFF).toByte()
    buffer[offset + 5] = (bits shr 40 and 0xFF).toByte()
    buffer[offset + 6] = (bits shr 48 and 0xFF).toByte()
    buffer[offset + 7] = (bits shr 56 and 0xFF).toByte()
}
