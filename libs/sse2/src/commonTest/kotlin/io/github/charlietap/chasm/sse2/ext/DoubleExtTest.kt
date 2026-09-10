package io.github.charlietap.chasm.sse2.ext

import kotlin.test.Test
import kotlin.test.assertEquals

class DoubleExtTest {

    @Test
    fun `can use fsqrt to compute the correct result`() {

        val input = "3868634135368364633"
        val expected = "4237908228221851551"

        val double = doubleFromBits(input)
        val actual = double.fsqrt()

        assertEquals(doubleFromBits(expected), actual)
    }

    companion object {
        fun doubleFromBits(input: String): Double {
            val bitPattern = input.toULong().toLong()
            return Double.fromBits(bitPattern)
        }
    }
}
