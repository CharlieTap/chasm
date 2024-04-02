package io.github.charlietap.chasm.decoder.wasm.decoder.vector

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.decoder.wasm.fixture.ioError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUIntReader
import io.github.charlietap.chasm.decoder.wasm.reader.IOErrorWasmFileReader
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryVectorLengthDecoderTest {

    @Test
    fun `can decode an encoded wasm vector length`() {

        val expected = Ok(VectorLength(16u))

        val reader = FakeUIntReader { Ok(16u) }

        val actual = BinaryVectorLengthDecoder(reader)

        assertEquals(expected, actual)
    }

    @Test
    fun `can catch io exceptions from the wasm file reader`() {
        val expected = ioError()
        val reader = IOErrorWasmFileReader(expected)

        val actual = BinaryByteVectorDecoder(reader)

        assertEquals(expected, actual)
    }
}
