package io.github.charlietap.chasm.runtime.host

import io.github.charlietap.chasm.InternalChasmApi

@InternalChasmApi
inline fun HostMemory.readUtf8String(
    memoryPointer: Int,
    stringLengthInBytes: Int,
): String {
    return read(
        buffer = ByteArray(stringLengthInBytes),
        memoryPointer = memoryPointer,
        bytesToRead = stringLengthInBytes,
    ).decodeToString()
}

@InternalChasmApi
fun HostMemory.readNullTerminatedUtf8String(memoryPointer: Int): String {

    val stringLengthInBytes = findNullByte(memoryPointer)

    return readUtf8String(memoryPointer, stringLengthInBytes)
}

@InternalChasmApi
inline fun HostMemory.writeUtf8String(memoryPointer: Int, string: String) {
    write(memoryPointer, string.encodeToByteArray())
}

private fun HostMemory.findNullByte(memoryPointer: Int): Int {
    var pointer = memoryPointer
    val memoryByteSize = byteSize
    val lastWordPointer = memoryByteSize - Long.SIZE_BYTES

    while (pointer <= lastWordPointer) {
        val byteOffset = readI64(pointer).indexOfNullByte()
        if (byteOffset != -1) {
            return pointer + byteOffset - memoryPointer
        }
        pointer += Long.SIZE_BYTES
    }

    while (pointer < memoryByteSize) {
        if (readI8(pointer) == 0.toByte()) {
            return pointer - memoryPointer
        }
        pointer++
    }

    throw IndexOutOfBoundsException()
}

private fun Long.indexOfNullByte(): Int {
    val containsNullByte =
        ((this - 0x0101010101010101L) and this.inv() and -0x7f7f7f7f7f80L) != 0L
    if (!containsNullByte) return -1

    repeat(Long.SIZE_BYTES) { byteOffset ->
        if ((this ushr (byteOffset * Byte.SIZE_BITS) and 0xFFL) == 0L) {
            return byteOffset
        }
    }
    return -1
}
