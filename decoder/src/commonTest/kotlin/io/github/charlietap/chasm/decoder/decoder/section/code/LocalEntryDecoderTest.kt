package io.github.charlietap.chasm.decoder.decoder.section.code

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.reader.FakeUIntReader
import io.github.charlietap.chasm.fixture.ast.type.i32ValueType
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
