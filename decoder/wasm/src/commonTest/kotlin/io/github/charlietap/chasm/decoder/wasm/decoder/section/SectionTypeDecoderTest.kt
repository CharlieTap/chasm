package io.github.charlietap.chasm.decoder.wasm.decoder.section

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.decoder.wasm.error.SectionDecodeError
import io.github.charlietap.chasm.decoder.wasm.fixture.decoderContext
import io.github.charlietap.chasm.decoder.wasm.fixture.ioError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUByteReader
import io.github.charlietap.chasm.decoder.wasm.reader.IOErrorWasmFileReader
import io.github.charlietap.chasm.decoder.wasm.section.SectionType
import kotlin.test.Test
import kotlin.test.assertEquals

class SectionTypeDecoderTest {

    @Test
    fun `can decode section types`() {

        val sectionTypes = SectionType.entries

        val sequence = sectionTypes.asSequence().iterator()

        val reader = FakeUByteReader {
            Ok(sequence.next().id)
        }
        val context = decoderContext(reader)

        val actual = (1..sectionTypes.size).map {
            SectionTypeDecoder(context)
        }

        val expected = sectionTypes.map(::Ok)

        assertEquals(expected, actual)
    }

    @Test
    fun `returns invalid section type error on mismatch`() {

        val invalidSectionType: UByte = 0x7Fu

        val reader = FakeUByteReader {
            Ok(invalidSectionType)
        }
        val context = decoderContext(reader)

        val actual = SectionTypeDecoder(context)

        assertEquals(Err(SectionDecodeError.UnknownSectionType(invalidSectionType)), actual)
    }

    @Test
    fun `returns io error when read fails`() {

        val err = ioError()
        val reader = IOErrorWasmFileReader(err)
        val context = decoderContext(reader)

        val actual = SectionTypeDecoder(context)

        assertEquals(err, actual)
    }
}
