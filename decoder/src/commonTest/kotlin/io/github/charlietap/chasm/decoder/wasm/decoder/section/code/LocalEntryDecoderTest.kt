package io.github.charlietap.chasm.decoder.wasm.decoder.section.code

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.section.code.LocalEntry
import io.github.charlietap.chasm.decoder.decoder.section.code.LocalEntryDecoder
import io.github.charlietap.chasm.decoder.wasm.fixture.decoderContext
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUIntReader
import io.github.charlietap.chasm.fixture.type.i32ValueType
import kotlin.test.Test
import kotlin.test.assertEquals

class LocalEntryDecoderTest {

    @Test
    fun `can decode a local`() {

        val valueType = i32ValueType()

        val count = 117u
        val expected = Ok(LocalEntry(count, valueType))

        val reader = FakeUIntReader {
            Ok(count)
        }
        val context = decoderContext(reader)

        val valueTypeDecoder: Decoder<ValueType> = {
            Ok(valueType)
        }

        val actual = LocalEntryDecoder(
            context = context,
            valueTypeDecoder = valueTypeDecoder,
        )

        assertEquals(expected, actual)
    }
}
