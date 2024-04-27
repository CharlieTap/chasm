@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.memory.ext

internal inline fun ByteArray.toShortLittleEndian(): Short =
    (
        (this[0].toInt() and 0xFF) or
            ((this[1].toInt() and 0xFF) shl 8)
    ).toShort()

internal inline fun ByteArray.toShortLittleEndian(offset: Int = 0): Short =
    (
        (this[offset].toInt() and 0xFF) or
            ((this[offset + 1].toInt() and 0xFF) shl 8)
    ).toShort()

internal inline fun ByteArray.toUShortLittleEndian(): UShort =
    (
        (this[0].toUByte().toUInt() and 0xFFu) or
            ((this[1].toUByte().toUInt() and 0xFFu) shl 8)
    ).toUShort()

internal inline fun ByteArray.toUShortLittleEndian(offset: Int = 0): UShort =
    (
        (this[offset].toUByte().toUInt() and 0xFFu) or
            ((this[offset + 1].toUByte().toUInt() and 0xFFu) shl 8)
    ).toUShort()

internal inline fun ByteArray.toIntLittleEndian(): Int {
    return (this[0].toInt() and 0xFF) or
        ((this[1].toInt() and 0xFF) shl 8) or
        ((this[2].toInt() and 0xFF) shl 16) or
        ((this[3].toInt() and 0xFF) shl 24)
}

internal inline fun ByteArray.toIntLittleEndian(offset: Int = 0): Int =
    (this[offset].toInt() and 0xFF) or
        ((this[offset + 1].toInt() and 0xFF) shl 8) or
        ((this[offset + 2].toInt() and 0xFF) shl 16) or
        ((this[offset + 3].toInt() and 0xFF) shl 24)

internal inline fun ByteArray.toIntSizedLittleEndian(size: Int): Int {
    var result = 0
    for (index in 0 until size) {
        result = result or ((this[index].toInt() and 0xFF) shl (8 * index))
    }
    if (size > 0 && this[size - 1] < 0) {
        for (index in size * 8 until 32) {
            result = result or (1 shl index)
        }
    }
    return result
}

internal inline fun ByteArray.toUIntLittleEndian(): UInt {
    return (this[0].toUInt() and 0xFFu) or
        ((this[1].toUInt() and 0xFFu) shl 8) or
        ((this[2].toUInt() and 0xFFu) shl 16) or
        ((this[3].toUInt() and 0xFFu) shl 24)
}

internal inline fun ByteArray.toUIntLittleEndian(offset: Int = 0): UInt {
    return (this[offset].toUInt() and 0xFFu) or
        ((this[offset + 1].toUInt() and 0xFFu) shl 8) or
        ((this[offset + 2].toUInt() and 0xFFu) shl 16) or
        ((this[offset + 3].toUInt() and 0xFFu) shl 24)
}

internal inline fun ByteArray.toUIntSizedLittleEndian(size: Int): UInt {
    var result = 0U
    for (index in 0 until size) {
        result = result or ((this[index].toUInt() and 0xFFU) shl (8 * index))
    }
    return result
}

internal inline fun ByteArray.toLongLittleEndian(): Long {
    return (this[0].toLong() and 0xFF) or
        ((this[1].toLong() and 0xFF) shl 8) or
        ((this[2].toLong() and 0xFF) shl 16) or
        ((this[3].toLong() and 0xFF) shl 24) or
        ((this[4].toLong() and 0xFF) shl 32) or
        ((this[5].toLong() and 0xFF) shl 40) or
        ((this[6].toLong() and 0xFF) shl 48) or
        ((this[7].toLong() and 0xFF) shl 56)
}

internal inline fun ByteArray.toLongLittleEndian(offset: Int = 0): Long =
    (this[offset].toLong() and 0xFF) or
        ((this[offset + 1].toLong() and 0xFF) shl 8) or
        ((this[offset + 2].toLong() and 0xFF) shl 16) or
        ((this[offset + 3].toLong() and 0xFF) shl 24) or
        ((this[offset + 4].toLong() and 0xFF) shl 32) or
        ((this[offset + 5].toLong() and 0xFF) shl 40) or
        ((this[offset + 6].toLong() and 0xFF) shl 48) or
        ((this[offset + 7].toLong() and 0xFF) shl 56)

internal inline fun ByteArray.toLongSizedLittleEndian(size: Int): Long {
    var result: Long = 0
    for (index in 0 until size) {
        result = result or ((this[index].toLong() and 0xFF) shl (8 * index))
    }
    if (size > 0 && this[size - 1] < 0) {
        for (index in size * 8 until 64) {
            result = result or (1L shl index)
        }
    }
    return result
}

internal inline fun ByteArray.toULongLittleEndian(): ULong {
    return (this[0].toULong() and 0xFFUL) or
        ((this[1].toULong() and 0xFFUL) shl 8) or
        ((this[2].toULong() and 0xFFUL) shl 16) or
        ((this[3].toULong() and 0xFFUL) shl 24) or
        ((this[4].toULong() and 0xFFUL) shl 32) or
        ((this[5].toULong() and 0xFFUL) shl 40) or
        ((this[6].toULong() and 0xFFUL) shl 48) or
        ((this[7].toULong() and 0xFFUL) shl 56)
}

internal inline fun ByteArray.toULongLittleEndian(offset: Int = 0): ULong {
    return (this[offset].toULong() and 0xFFuL) or
        ((this[offset + 1].toULong() and 0xFFuL) shl 8) or
        ((this[offset + 2].toULong() and 0xFFuL) shl 16) or
        ((this[offset + 3].toULong() and 0xFFuL) shl 24) or
        ((this[offset + 4].toULong() and 0xFFuL) shl 32) or
        ((this[offset + 5].toULong() and 0xFFuL) shl 40) or
        ((this[offset + 6].toULong() and 0xFFuL) shl 48) or
        ((this[offset + 7].toULong() and 0xFFuL) shl 56)
}

internal inline fun ByteArray.toULongSizedLittleEndian(size: Int): ULong {
    var result = 0UL
    for (index in 0 until size) {
        result = result or ((this[index].toULong() and 0xFFUL) shl (8 * index))
    }
    return result
}

internal inline fun ByteArray.toFloatLittleEndian(offset: Int = 0): Float =
    Float.fromBits(this.toIntLittleEndian(offset))

internal inline fun ByteArray.toDoubleLittleEndian(offset: Int = 0): Double =
    Double.fromBits(this.toLongLittleEndian(offset))
