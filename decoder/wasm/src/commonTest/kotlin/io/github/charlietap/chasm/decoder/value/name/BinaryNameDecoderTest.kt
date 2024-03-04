package io.github.charlietap.chasm.decoder.value.name

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.decoder.vector.ByteVector
import io.github.charlietap.chasm.decoder.vector.ByteVectorDecoder
import io.github.charlietap.chasm.fixture.ioError
import io.github.charlietap.chasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.reader.IOErrorWasmFileReader
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryNameDecoderTest {

    @Test
    fun `can decode an encoded wasm vector value`() {

        val nameValue = NameValue(VALID_VECTOR.asByteArray().decodeToString())
        val expected = Ok(nameValue)

        val reader = FakeWasmBinaryReader()
        val vectorDecoder: ByteVectorDecoder = {
            Ok(ByteVector(VALID_VECTOR, 5u))
        }

        val actual = BinaryNameValueDecoder(reader, vectorDecoder)

        assertEquals(expected, actual)
    }

    @Test
    fun `can catch io exceptions from the wasm file reader`() {
        val expected = ioError()
        val reader = IOErrorWasmFileReader(expected)

        val actual = BinaryNameValueDecoder(reader)

        assertEquals(expected, actual)
    }

    companion object {
        private val VALID_VECTOR = ubyteArrayOf(
            'h'.code.toUByte(),
            'e'.code.toUByte(),
            'l'.code.toUByte(),
            'l'.code.toUByte(),
            'o'.code.toUByte(),
        )
    }
}
