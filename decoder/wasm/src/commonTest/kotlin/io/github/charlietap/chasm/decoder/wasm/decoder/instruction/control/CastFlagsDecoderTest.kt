package io.github.charlietap.chasm.decoder.wasm.decoder.instruction.control

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.decoder.wasm.fixture.decoderContext
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUByteReader
import kotlin.test.Test
import kotlin.test.assertEquals

class CastFlagsDecoderTest {

    @Test
    fun `can decode an encoded cast flags`() {

        val castFlagsMap = mapOf(
            NON_NULL_BOTH_CAST_FLAGS to CastFlags(Nullability.NON_NULL, Nullability.NON_NULL),
            NULL_TO_NON_NULL_CAST_FLAGS to CastFlags(Nullability.NULL, Nullability.NON_NULL),
            NON_NULL_TO_NULL_CAST_FLAGS to CastFlags(Nullability.NON_NULL, Nullability.NULL),
            NULL_BOTH_CAST_FLAGS to CastFlags(Nullability.NULL, Nullability.NULL),
        )

        castFlagsMap.forEach { (castFlagByte, castFlags) ->

            val reader = FakeUByteReader { Ok(castFlagByte) }
            val context = decoderContext(reader)
            val expected = Ok(castFlags)

            val actual = CastFlagsDecoder(context)

            assertEquals(expected, actual)
        }
    }
}
