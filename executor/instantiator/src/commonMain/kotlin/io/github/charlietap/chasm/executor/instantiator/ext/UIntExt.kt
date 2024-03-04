package io.github.charlietap.chasm.executor.instantiator.ext

internal fun UInt.copyInto(buffer: ByteArray, offset: Int) {
    buffer[offset] = (this and 0xFFu).toByte()
    buffer[offset + 1] = (this shr 8 and 0xFFu).toByte()
    buffer[offset + 2] = (this shr 16 and 0xFFu).toByte()
    buffer[offset + 3] = (this shr 24 and 0xFFu).toByte()
}
