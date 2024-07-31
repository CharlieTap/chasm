package io.github.charlietap.chasm.decoder.ext

import com.github.michaelbull.result.Ok
import kotlin.test.Test
import kotlin.test.assertEquals

class ByteArrayExtTest {

    @Test
    fun `can decode an IEEE 754 encoded float`() {
        val encoded = byteArrayOf(0x00.toByte(), 0x00.toByte(), 0xC0.toByte(), 0x3F.toByte())
        val expected = Ok(1.5f)
        val actual = encoded.toFloatLe()

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an IEEE 754 encoded double`() {
        val encoded =
            byteArrayOf(161.toByte(), 248.toByte(), 49.toByte(), 230.toByte(), 214.toByte(), 28.toByte(), 200.toByte(), 64.toByte())
        val expected = Ok(12345.6789)
        val actual = encoded.toDoubleLe()

        assertEquals(expected, actual)
    }
}
