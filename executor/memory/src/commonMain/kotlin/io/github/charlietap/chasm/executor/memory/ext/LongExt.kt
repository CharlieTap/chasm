@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.memory.ext

internal inline fun Long.copyInto(buffer: ByteArray, offset: Int) {
    buffer[offset] = (this and 0xFF).toByte()
    buffer[offset + 1] = (this shr 8 and 0xFF).toByte()
    buffer[offset + 2] = (this shr 16 and 0xFF).toByte()
    buffer[offset + 3] = (this shr 24 and 0xFF).toByte()
    buffer[offset + 4] = (this shr 32 and 0xFF).toByte()
    buffer[offset + 5] = (this shr 40 and 0xFF).toByte()
    buffer[offset + 6] = (this shr 48 and 0xFF).toByte()
    buffer[offset + 7] = (this shr 56 and 0xFF).toByte()
}

internal inline fun Long.copyInto(buffer: ByteArray, offset: Int, size: Int) = repeat(size) { index ->
    buffer[offset + index] = (this shr (8 * index) and 0xFFL).toByte()
}
