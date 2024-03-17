package io.github.charlietap.chasm.decoder.wasm.ext

import io.github.charlietap.chasm.decoder.wasm.const.Leb128
import kotlin.test.Test
import kotlin.test.assertEquals

class SequenceExtTest {

    private val signedIntegerInput by lazy {
        listOf(
            Leb128.Integer.ONE_BYTE_SIGNED_POSITIVE to 1,
            Leb128.Integer.TWO_BYTES_SIGNED_POSITIVE to 128,
            Leb128.Integer.THREE_BYTES_SIGNED_POSITIVE to 16384,
            Leb128.Integer.FOUR_BYTES_SIGNED_POSITIVE to 2097152,
            Leb128.Integer.FIVE_BYTES_SIGNED_POSITIVE to Int.MAX_VALUE,
            Leb128.Integer.ONE_BYTE_SIGNED_NEGATIVE to -1,
            Leb128.Integer.TWO_BYTES_SIGNED_NEGATIVE to -129,
            Leb128.Integer.THREE_BYTES_SIGNED_NEGATIVE to -16385,
            Leb128.Integer.FOUR_BYTES_SIGNED_NEGATIVE to -2097153,
            Leb128.Integer.FIVE_BYTES_SIGNED_NEGATIVE to Int.MIN_VALUE,
        )
    }

    private val unsignedIntegerInput by lazy {
        listOf(
            Leb128.Integer.ONE_BYTE_UNSIGNED to 127u,
            Leb128.Integer.TWO_BYTES_UNSIGNED to 128u,
            Leb128.Integer.THREE_BYTES_UNSIGNED to 16384u,
            Leb128.Integer.FOUR_BYTES_UNSIGNED to 2097152u,
            Leb128.Integer.FIVE_BYTES_UNSIGNED to 268435456u,
        )
    }

    private val signedLongInput by lazy {
        listOf(
            Leb128.Long.SIX_BYTES_SIGNED_POSITIVE to 34359738368,
            Leb128.Long.SIX_BYTES_SIGNED_NEGATIVE to -34359738369,
            Leb128.Long.TEN_BYTES_SIGNED_POSITIVE to Long.MAX_VALUE,
            Leb128.Long.TEN_BYTES_SIGNED_NEGATIVE to Long.MIN_VALUE,
        ) + signedIntegerInput.map { (first, second) -> first to second.toLong() }
    }

    @Test
    fun `can decode leb128 encoded unsigned integers`() {
        unsignedIntegerInput.forEach { (encoded, expected) ->

            val actual = encoded.asSequence().toUIntLeb128()

            assertEquals(expected, actual)
        }
    }

    @Test
    fun `can decode leb128 encoded signed integers`() {
        signedIntegerInput.forEach { (encoded, expected) ->

            val actual = encoded.asSequence().toIntLeb128()

            assertEquals(expected, actual)
        }
    }

    @Test
    fun `can decode leb128 encoded signed longs`() {
        signedLongInput.forEach { (encoded, expected) ->

            val actual = encoded.asSequence().toLongLeb128()

            assertEquals(expected, actual)
        }
    }
}
