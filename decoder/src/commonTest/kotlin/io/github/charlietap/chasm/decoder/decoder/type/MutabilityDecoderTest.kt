package io.github.charlietap.chasm.decoder.decoder.type

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.Mutability
import io.github.charlietap.chasm.decoder.error.TypeDecodeError
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.reader.FakeUByteReader
import kotlin.test.Test
import kotlin.test.assertEquals

class MutabilityDecoderTest {

    @Test
    fun `can decode an encoded const mutability`() {

        val reader = FakeUByteReader {
            Ok(CONST_MUTABILITY)
        }
        val context = decoderContext(reader)

        val expected = Ok(Mutability.Const)

        val actual = MutabilityDecoder(context)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an encoded var mutability`() {

        val reader = FakeUByteReader {
            Ok(VAR_MUTABILITY)
        }
        val context = decoderContext(reader)

        val expected = Ok(Mutability.Var)

        val actual = MutabilityDecoder(context)

        assertEquals(expected, actual)
    }

    @Test
    fun `returns an InvalidMutability error unrecognised byte is encountered`() {

        val reader = FakeUByteReader {
            Ok(UByte.MAX_VALUE)
        }
        val context = decoderContext(reader)

        val expected = Err(TypeDecodeError.UnknownMutabilityFlag(UByte.MAX_VALUE))

        val actual = MutabilityDecoder(context)

        assertEquals(expected, actual)
    }
}
