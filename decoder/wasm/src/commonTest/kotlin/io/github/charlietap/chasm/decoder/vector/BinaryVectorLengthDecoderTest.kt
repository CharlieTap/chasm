package io.github.charlietap.chasm.decoder.vector

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.fixture.ioError
import io.github.charlietap.chasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.reader.IOErrorWasmFileReader
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryVectorLengthDecoderTest {

    @Test
    fun `can decode an encoded wasm vector length`() {

        val expected = Ok(VectorLength(16u))

        val int: () -> Ok<UInt> = { Ok(16u) }
        val reader = FakeWasmBinaryReader(fakeUIntReader = int)

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
