package io.github.charlietap.chasm.decoder.ext

private const val CONTINUATION_BIT = 1 shl 7
private const val SIGN_BIT = 1 shl 6

private const val MAX_SHIFT_INT = 35
private const val MAX_SHIFT_LONG = 70

internal fun Sequence<Byte>.toIntLeb128(): Int {
    val iterator = iterator()
    var result = 0
    var shift = 0
    var byte: Int

    do {
        byte = iterator.next().toInt()
        val value = byte and 0x7F
        result = result or (value shl shift)
        shift += 7
    } while (byte and CONTINUATION_BIT != 0)

    if (shift >= Int.SIZE_BITS) {
        if (shift > MAX_SHIFT_INT) {
            throw IllegalArgumentException("integer too large")
        }

        val mask = ((-1 shl ((Int.SIZE_BITS % 7) - 1)) and 0x7F)
        if (byte and mask != 0 && byte < mask) {
            throw IllegalArgumentException("integer too large or contains unnecessary high bits")
        }
    }

    if (byte and SIGN_BIT != 0 && shift < Int.SIZE_BITS) {
        result = result or (-1 shl shift)
    }

    return result
}

internal fun Sequence<Byte>.toLongLeb128(): Long {
    val iterator = iterator()
    var result: Long = 0
    var shift = 0
    var byte: Int

    do {
        byte = iterator.next().toInt()
        val value = byte and 0x7F
        result = result or (value.toLong() shl shift)
        shift += 7
    } while (byte and CONTINUATION_BIT != 0)

    if (shift >= Long.SIZE_BITS) {
        if (shift > MAX_SHIFT_LONG) {
            throw IllegalArgumentException("integer too large")
        }

        val mask = ((-1 shl ((Long.SIZE_BITS % 7) - 1)) and 0x7F)
        if (byte and mask != 0 && byte < mask) {
            throw IllegalArgumentException("integer too large or contains unnecessary high bits")
        }
    }

    if (byte and SIGN_BIT != 0 && shift < Long.SIZE_BITS) {
        result = result or (-1L shl shift)
    }

    return result
}

internal fun Sequence<UByte>.toUIntLeb128(): UInt {

    val iter = iterator()
    var result = 0u
    var shift = 0
    var byte: UByte

    do {
        byte = iter.next()
        val value = (byte and 0x7Fu).toUInt()
        result = result or (value shl shift)
        shift += 7
    } while (byte and CONTINUATION_BIT.toUByte() != 0u.toUByte())

    if (shift > MAX_SHIFT_INT || (shift == MAX_SHIFT_INT && byte and 0xF0u != 0.toUByte())) {
        throw IllegalArgumentException("unsigned integer too large")
    }

    return result
}

internal fun Sequence<UByte>.toULongLeb128(): ULong {
    val iter = iterator()
    var result = 0uL
    var shift = 0
    var byte: UByte

    do {
        byte = iter.next()
        val value = (byte and 0x7Fu).toULong()
        result = result or (value shl shift)
        shift += 7
    } while (byte and CONTINUATION_BIT.toUByte() != 0u.toUByte())

    val mask = ((-1 shl (Long.SIZE_BITS % 7)) and 0x7F).toUByte()
    if (shift > MAX_SHIFT_LONG || (shift == MAX_SHIFT_LONG && byte and mask != 0u.toUByte())) {
        throw IllegalArgumentException("unsigned long too large")
    }

    return result
}
