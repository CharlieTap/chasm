package io.github.charlietap.chasm.decoder.wasm.decoder.section.code

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.decoder.wasm.decoder.type.value.ValueTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUIntReader
import io.github.charlietap.chasm.fixture.type.i32ValueType
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryLocalEntryDecoderTest {

    @Test
    fun `can decode a local`() {

        val valueType = i32ValueType()

        val count = 117u
        val expected = Ok(LocalEntry(count, valueType))

        val reader = FakeUIntReader {
            Ok(count)
        }

        val valueTypeDecoder: ValueTypeDecoder = {
            Ok(valueType)
        }

        val actual = BinaryLocalEntryDecoder(
            reader = reader,
            valueTypeDecoder = valueTypeDecoder,
        )

        assertEquals(expected, actual)
    }
}
