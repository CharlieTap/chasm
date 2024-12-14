package io.github.charlietap.chasm.decoder.decoder.type.global

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.ast.type.Mutability
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.fixture.ast.type.mutability
import io.github.charlietap.chasm.fixture.ast.type.valueType
import kotlin.test.Test
import kotlin.test.assertEquals

class GlobalTypeDecoderTest {

    @Test
    fun `can decode an encoded global type`() {

        val reader = FakeWasmBinaryReader()
        val context = decoderContext(reader)

        val valueType = valueType()
        val valueTypeDecoder: Decoder<ValueType> = { _context ->
            assertEquals(context, _context)
            Ok(valueType)
        }

        val mutability = mutability()
        val mutabilityDecoder: Decoder<Mutability> = { _context ->
            assertEquals(context, _context)
            Ok(mutability)
        }

        val expected = Ok(GlobalType(valueType, mutability))

        val actual = GlobalTypeDecoder(
            context,
            valueTypeDecoder,
            mutabilityDecoder,
        )

        assertEquals(expected, actual)
    }
}
