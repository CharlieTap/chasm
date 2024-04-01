package io.github.charlietap.chasm.decoder.wasm.decoder.type

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.Mutability
import io.github.charlietap.chasm.decoder.wasm.error.TypeDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUByteReader
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryMutabilityDecoderTest {

    @Test
    fun `can decode an encoded const mutability`() {

        val reader = FakeUByteReader {
            Ok(CONST_MUTABILITY)
        }

        val expected = Ok(Mutability.Const)

        val actual = BinaryMutabilityDecoder(reader)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an encoded var mutability`() {

        val reader = FakeUByteReader {
            Ok(VAR_MUTABILITY)
        }

        val expected = Ok(Mutability.Var)

        val actual = BinaryMutabilityDecoder(reader)

        assertEquals(expected, actual)
    }

    @Test
    fun `returns an InvalidMutability error unrecognised byte is encountered`() {

        val reader = FakeUByteReader {
            Ok(UByte.MAX_VALUE)
        }

        val expected = Err(TypeDecodeError.UnknownMutabilityFlag(UByte.MAX_VALUE))

        val actual = BinaryMutabilityDecoder(reader)

        assertEquals(expected, actual)
    }
}
