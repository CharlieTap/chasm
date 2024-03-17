package io.github.charlietap.chasm.decoder.wasm.ext

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
    } while (byte and 0x80 != 0)

    if (byte and 0x40 != 0 && shift < 32) {
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
    } while (byte and 0x80 != 0)

    if (byte and 0x40 != 0 && shift < 64) {
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
        result = result or ((byte and 0x7Fu).toUInt() shl shift)
        shift += 7
    } while (byte and 0x80u.toUByte() != 0u.toUByte())

    return result
}
