package io.github.charlietap.chasm.executor.memory.ext

internal fun ByteArray.toIntLittleEndian(): Int {
    var result = 0
    for (index in indices) {
        result = result or ((this[index].toInt() and 0xFF) shl (8 * index))
    }
    return result
}

internal fun ByteArray.toIntLittleEndian(offset: Int = 0): Int =
    (this[offset].toInt() and 0xFF) or
        ((this[offset + 1].toInt() and 0xFF) shl 8) or
        ((this[offset + 2].toInt() and 0xFF) shl 16) or
        ((this[offset + 3].toInt() and 0xFF) shl 24)

internal fun ByteArray.toUIntLittleEndian(): UInt {
    var result = 0U
    for (index in indices) {
        result = result or ((this[index].toUInt() and 0xFFU) shl (8 * index))
    }
    return result
}

internal fun ByteArray.toUIntLittleEndian(offset: Int = 0): UInt {
    return (this[offset].toUInt() and 0xFFu) or
        ((this[offset + 1].toUInt() and 0xFFu) shl 8) or
        ((this[offset + 2].toUInt() and 0xFFu) shl 16) or
        ((this[offset + 3].toUInt() and 0xFFu) shl 24)
}

internal fun ByteArray.toLongLittleEndian(): Long {
    var result: Long = 0
    for (index in indices) {
        result = result or ((this[index].toLong() and 0xFF) shl (8 * index))
    }
    return result
}

internal fun ByteArray.toLongLittleEndian(offset: Int = 0): Long =
    (this[offset].toLong() and 0xFF) or
        ((this[offset + 1].toLong() and 0xFF) shl 8) or
        ((this[offset + 2].toLong() and 0xFF) shl 16) or
        ((this[offset + 3].toLong() and 0xFF) shl 24) or
        ((this[offset + 4].toLong() and 0xFF) shl 32) or
        ((this[offset + 5].toLong() and 0xFF) shl 40) or
        ((this[offset + 6].toLong() and 0xFF) shl 48) or
        ((this[offset + 7].toLong() and 0xFF) shl 56)

internal fun ByteArray.toULongLittleEndian(): ULong {
    var result = 0UL
    for (index in indices) {
        result = result or ((this[index].toULong() and 0xFFUL) shl (8 * index))
    }
    return result
}

internal fun ByteArray.toULongLittleEndian(offset: Int = 0): ULong {
    return (this[offset].toULong() and 0xFFuL) or
        ((this[offset + 1].toULong() and 0xFFuL) shl 8) or
        ((this[offset + 2].toULong() and 0xFFuL) shl 16) or
        ((this[offset + 3].toULong() and 0xFFuL) shl 24) or
        ((this[offset + 4].toULong() and 0xFFuL) shl 32) or
        ((this[offset + 5].toULong() and 0xFFuL) shl 40) or
        ((this[offset + 6].toULong() and 0xFFuL) shl 48) or
        ((this[offset + 7].toULong() and 0xFFuL) shl 56)
}

internal fun ByteArray.toFloatLittleEndian(offset: Int = 0): Float =
    Float.fromBits(this.toIntLittleEndian(offset))

internal fun ByteArray.toDoubleLittleEndian(offset: Int = 0): Double =
    Double.fromBits(this.toLongLittleEndian(offset))
