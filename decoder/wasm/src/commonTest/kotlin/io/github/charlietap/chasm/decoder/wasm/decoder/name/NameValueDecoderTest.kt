package io.github.charlietap.chasm.decoder.wasm.decoder.name

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.decoder.wasm.decoder.value.name.NameValueDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.ByteVector
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.ByteVectorDecoder
import io.github.charlietap.chasm.decoder.wasm.fixture.decoderContext
import io.github.charlietap.chasm.decoder.wasm.fixture.ioError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.reader.IOErrorWasmFileReader
import kotlin.test.Test
import kotlin.test.assertEquals

class NameValueDecoderTest {

    @Test
    fun `can decode an encoded wasm vector value`() {

        val nameValue = NameValue(VALID_VECTOR.asByteArray().decodeToString())
        val expected = Ok(nameValue)

        val reader = FakeWasmBinaryReader()
        val context = decoderContext(reader)
        val vectorDecoder: ByteVectorDecoder = {
            Ok(ByteVector(VALID_VECTOR, 5u))
        }

        val actual = NameValueDecoder(context, vectorDecoder)

        assertEquals(expected, actual)
    }

    @Test
    fun `can catch io exceptions from the wasm file reader`() {
        val expected = ioError()
        val reader = IOErrorWasmFileReader(expected)
        val context = decoderContext(reader)

        val actual = NameValueDecoder(context)

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
