package io.github.charlietap.chasm.ext

fun Int.toSignedLeb128(): ByteArray {
    var value = this
    val result = ArrayList<Byte>()
    var more = true
    while (more) {
        var byte = (value and 0x7F)
        value = value ushr 7
        if ((value == 0 && (byte and 0x40) == 0) || (value == -1 && (byte and 0x40) != 0)) {
            more = false
        } else {
            byte = byte or 0x80
        }
        result.add(byte.toByte())
    }
    return result.toByteArray()
}
