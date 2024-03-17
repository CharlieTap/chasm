@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.memory.ext

internal inline fun Int.copyInto(buffer: ByteArray, offset: Int) {
    buffer[offset] = (this and 0xFF).toByte()
    buffer[offset + 1] = (this shr 8 and 0xFF).toByte()
    buffer[offset + 2] = (this shr 16 and 0xFF).toByte()
    buffer[offset + 3] = (this shr 24 and 0xFF).toByte()
}

internal inline fun Int.copyInto(buffer: ByteArray, offset: Int, size: Int) = repeat(size) { index ->
    buffer[offset + index] = (this shr (8 * index) and 0xFF).toByte()
}
