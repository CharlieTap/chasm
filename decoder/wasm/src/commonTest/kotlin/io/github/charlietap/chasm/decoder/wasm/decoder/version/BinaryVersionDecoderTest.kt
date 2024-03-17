package io.github.charlietap.chasm.decoder.wasm.decoder.version

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.fixture.ioError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUBytesReader
import io.github.charlietap.chasm.decoder.wasm.reader.IOErrorWasmFileReader
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryVersionDecoderTest {

    @Test
    fun `can decode version one`() {

        val reader = FakeUBytesReader { _ ->
            Ok(ubyteArrayOf(0x01u, 0x00u, 0x00u, 0x00u))
        }

        val actual = BinaryVersionDecoder(reader)

        assertEquals(Ok(Version.One), actual)
    }

    @Test
    fun `returns unsupported version error when version is not found`() {

        val unsupportedVersion = ubyteArrayOf(0x00u, 0x00u, 0x00u, 0x00u)

        val reader = FakeUBytesReader { _ ->
            Ok(unsupportedVersion)
        }

        val actual = BinaryVersionDecoder(reader)

        assertEquals(Err(WasmDecodeError.UnsupportedVersion(unsupportedVersion)), actual)
    }

    @Test
    fun `returns io error when read fails`() {

        val err = ioError()
        val reader = IOErrorWasmFileReader(err)

        val actual = BinaryVersionDecoder(reader)

        assertEquals(err, actual)
    }
}
