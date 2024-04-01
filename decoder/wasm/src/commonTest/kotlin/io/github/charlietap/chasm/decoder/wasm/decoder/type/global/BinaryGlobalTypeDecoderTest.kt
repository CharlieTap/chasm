package io.github.charlietap.chasm.decoder.wasm.decoder.type.global

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.decoder.wasm.decoder.type.MutabilityDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.value.ValueTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.fixture.type.mutability
import io.github.charlietap.chasm.fixture.type.valueType
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryGlobalTypeDecoderTest {

    @Test
    fun `can decode an encoded global type`() {

        val reader = FakeWasmBinaryReader()

        val valueType = valueType()
        val valueTypeDecoder: ValueTypeDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(valueType)
        }

        val mutability = mutability()
        val mutabilityDecoder: MutabilityDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(mutability)
        }

        val expected = Ok(GlobalType(valueType, mutability))

        val actual = BinaryGlobalTypeDecoder(
            reader,
            valueTypeDecoder,
            mutabilityDecoder,
        )

        assertEquals(expected, actual)
    }
}
