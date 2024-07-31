package io.github.charlietap.chasm.decoder.decoder.section.custom

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.Custom
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.fixture.ioError
import io.github.charlietap.chasm.decoder.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.decoder.reader.IOErrorWasmFileReader
import io.github.charlietap.chasm.decoder.section.CustomSection
import io.github.charlietap.chasm.decoder.section.SectionSize
import kotlin.test.Test
import kotlin.test.assertEquals

class CustomSectionDecoderTest {

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
        val context = decoderContext(reader, sectionSize)

        val nameValueDecoder: Decoder<NameValue> = {
            Ok(nameValue)
        }
        val actual = CustomSectionDecoder(context, nameValueDecoder)

        assertEquals(expected, actual)
    }

    @Test
    fun `can catch io exceptions from the wasm file reader`() {
        val expected = ioError()
        val reader = IOErrorWasmFileReader(expected)
        val context = decoderContext(reader)

        val actual = CustomSectionDecoder(context)

        assertEquals(expected, actual)
    }
}
