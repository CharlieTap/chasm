package io.github.charlietap.chasm.decoder.wasm.decoder.section.custom

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.Custom
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.decoder.wasm.decoder.value.name.NameValueDecoder
import io.github.charlietap.chasm.decoder.wasm.fixture.ioError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.reader.IOErrorWasmFileReader
import io.github.charlietap.chasm.decoder.wasm.section.CustomSection
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryCustomSectionDecoderTest {

    @Test
    fun `can decode an encoded custom section`() {

        val sectionSize = SectionSize(14u)
        val nameValue = NameValue("custom")
        val sectionBytes = "section".encodeToByteArray().asUByteArray()
        val custom = Custom(nameValue, sectionBytes)
        val expected = Ok(CustomSection(custom))

        val reader = FakeWasmBinaryReader(
            fakeUBytesReader = { bytesRequested ->
                assertEquals(7u, bytesRequested)
                Ok(sectionBytes)
            },
            fakePositionReader = sequenceOf(0u, 7u).iterator()::next,
        )

        val nameValueDecoder: NameValueDecoder = {
            Ok(nameValue)
        }
        val decoder = BinaryCustomSectionDecoder(nameValueDecoder)

        val actual = decoder(reader, sectionSize)

        assertEquals(expected, actual)
    }

    @Test
    fun `can catch io exceptions from the wasm file reader`() {
        val expected = ioError()
        val reader = IOErrorWasmFileReader(expected)

        val decoder = BinaryCustomSectionDecoder()
        val actual = decoder(reader, SectionSize(0u))

        assertEquals(expected, actual)
    }
}
