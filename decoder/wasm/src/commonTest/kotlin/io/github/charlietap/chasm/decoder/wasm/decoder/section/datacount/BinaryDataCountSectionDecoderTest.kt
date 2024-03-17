package io.github.charlietap.chasm.decoder.wasm.decoder.section.datacount

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUIntReader
import io.github.charlietap.chasm.decoder.wasm.section.DataCountSection
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryDataCountSectionDecoderTest {

    @Test
    fun `can decode an encoded data count section`() {

        val reader = FakeUIntReader {
            Ok(117u)
        }

        val decoder = BinaryDataCountSectionDecoder()

        val expected = Ok(DataCountSection(117u))
        val actual = decoder(reader, SectionSize(0u))

        assertEquals(expected, actual)
    }
}
