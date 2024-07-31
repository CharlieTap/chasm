package io.github.charlietap.chasm.decoder.decoder.version

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.fixture.ioError
import io.github.charlietap.chasm.decoder.reader.FakeUBytesReader
import io.github.charlietap.chasm.decoder.reader.IOErrorWasmFileReader
import kotlin.test.Test
import kotlin.test.assertEquals

class VersionDecoderTest {

    @Test
    fun `can decode version one`() {

        val reader = FakeUBytesReader { _ ->
            Ok(ubyteArrayOf(0x01u, 0x00u, 0x00u, 0x00u))
        }

        val context = decoderContext(reader)
        val actual = VersionDecoder(context)

        assertEquals(Ok(Version.One), actual)
    }

    @Test
    fun `returns unsupported version error when version is not found`() {

        val unsupportedVersion = ubyteArrayOf(0x00u, 0x00u, 0x00u, 0x00u)

        val reader = FakeUBytesReader { _ ->
            Ok(unsupportedVersion)
        }

        val context = decoderContext(reader)
        val actual = VersionDecoder(context)

        assertEquals(Err(WasmDecodeError.UnsupportedVersion(unsupportedVersion)), actual)
    }

    @Test
    fun `returns io error when read fails`() {

        val err = ioError()
        val reader = IOErrorWasmFileReader(err)

        val context = decoderContext(reader)
        val actual = VersionDecoder(context)

        assertEquals(err, actual)
    }
}
