package io.github.charlietap.chasm.const

object Leb128 {

    object Integer {
        val ONE_BYTE_UNSIGNED: UByteArray = ubyteArrayOf(0x7Fu) // 127
        val TWO_BYTES_UNSIGNED: UByteArray = ubyteArrayOf(0x80u, 0x01u) // 128
        val THREE_BYTES_UNSIGNED: UByteArray = ubyteArrayOf(0x80u, 0x80u, 0x01u) // 16384
        val FOUR_BYTES_UNSIGNED: UByteArray = ubyteArrayOf(0x80u, 0x80u, 0x80u, 0x01u) // 2097152
        val FIVE_BYTES_UNSIGNED: UByteArray = ubyteArrayOf(0x80u, 0x80u, 0x80u, 0x80u, 0x01u) // 268435456

        val ONE_BYTE_SIGNED_POSITIVE: ByteArray = byteArrayOf(0x01) // 1
        val TWO_BYTES_SIGNED_POSITIVE: ByteArray = byteArrayOf(0x80.toByte(), 0x01) // 128
        val THREE_BYTES_SIGNED_POSITIVE: ByteArray = byteArrayOf(0x80.toByte(), 0x80.toByte(), 0x01) // 16383
        val FOUR_BYTES_SIGNED_POSITIVE: ByteArray = byteArrayOf(0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 0x01) // 2097152
        val FIVE_BYTES_SIGNED_POSITIVE: ByteArray = byteArrayOf(
            0xFF.toByte(),
            0xFF.toByte(),
            0xFF.toByte(),
            0xFF.toByte(),
            0x07,
        ) // 2147483647 max i32

        val ONE_BYTE_SIGNED_NEGATIVE: ByteArray = byteArrayOf(0x7F) // -1
        val TWO_BYTES_SIGNED_NEGATIVE: ByteArray = byteArrayOf(0xFF.toByte(), 0x7E) // -129
        val THREE_BYTES_SIGNED_NEGATIVE: ByteArray = byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0x7E) // -16385
        val FOUR_BYTES_SIGNED_NEGATIVE: ByteArray = byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0x7E) // -2097153
        val FIVE_BYTES_SIGNED_NEGATIVE: ByteArray = byteArrayOf(
            0x80.toByte(),
            0x80.toByte(),
            0x80.toByte(),
            0x80.toByte(),
            0x78,
        ) // -2147483648 min i32
    }

    object Long {
        val SIX_BYTES_SIGNED_POSITIVE: ByteArray =
            byteArrayOf(0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 0x01) // 34359738368
        val TEN_BYTES_SIGNED_POSITIVE: ByteArray =
            byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0x00) // 9223372036854775807 max i64
        val SIX_BYTES_SIGNED_NEGATIVE: ByteArray =
            byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0x7E) // -34359738369
        val TEN_BYTES_SIGNED_NEGATIVE: ByteArray =
            byteArrayOf(0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 0x7f) // -9223372036854775808 min i64
    }
}
