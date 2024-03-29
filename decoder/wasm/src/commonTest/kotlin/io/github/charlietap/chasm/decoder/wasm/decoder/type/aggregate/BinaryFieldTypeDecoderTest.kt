package io.github.charlietap.chasm.decoder.wasm.decoder.type.aggregate

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.FieldType
import io.github.charlietap.chasm.decoder.wasm.decoder.type.MutabilityDecoder
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.fixture.type.mutability
import io.github.charlietap.chasm.fixture.type.storageType
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryFieldTypeDecoderTest {

    @Test
    fun `can decode an encoded field type`() {

        val reader = FakeWasmBinaryReader()

        val storageType = storageType()
        val storageTypeDecoder: StorageTypeDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(storageType)
        }

        val mutability = mutability()
        val mutabilityDecoder: MutabilityDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(mutability)
        }

        val expected = Ok(FieldType(storageType, mutability))

        val actual = BinaryFieldTypeDecoder(
            reader,
            storageTypeDecoder,
            mutabilityDecoder,
        )

        assertEquals(expected, actual)
    }
}
