package io.github.charlietap.chasm.decoder.decoder.vector

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.fixture.ioError
import io.github.charlietap.chasm.decoder.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.decoder.reader.IOErrorWasmFileReader
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryVectorDecoderTest {

    @Test
    fun `can decode an encoded wasm vector`() {

        val expected = Ok(ByteVector(VALID_VECTOR, 16u))

        val int: () -> Result<UInt, WasmDecodeError> = { Ok(16u) }
        val bytes: (UInt) -> Result<UByteArray, WasmDecodeError> = { _ -> Ok(VALID_VECTOR) }

        val reader = FakeWasmBinaryReader(fakeUBytesReader = bytes, fakeUIntReader = int)

        val actual = BinaryByteVectorDecoder(reader)

        assertEquals(expected, actual)
    }

    @Test
    fun `can catch io exceptions from the wasm file reader`() {
        val expected = ioError()
        val reader = IOErrorWasmFileReader(expected)

        val actual = BinaryByteVectorDecoder(reader)

        assertEquals(expected, actual)
    }

    companion object {
        private val VALID_VECTOR = ubyteArrayOf(
            0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
            0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        )
    }
}
