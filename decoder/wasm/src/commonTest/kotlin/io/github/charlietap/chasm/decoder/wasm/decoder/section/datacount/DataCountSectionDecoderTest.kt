package io.github.charlietap.chasm.decoder.wasm.decoder.section.datacount

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.decoder.wasm.fixture.decoderContext
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUIntReader
import io.github.charlietap.chasm.decoder.wasm.section.DataCountSection
import kotlin.test.Test
import kotlin.test.assertEquals

class DataCountSectionDecoderTest {

    @Test
    fun `can decode an encoded data count section`() {

        val reader = FakeUIntReader {
            Ok(117u)
        }

        val context = decoderContext(reader)
        val actual = DataCountSectionDecoder(context)

        val expected = Ok(DataCountSection(117u))

        assertEquals(expected, actual)
    }
}
