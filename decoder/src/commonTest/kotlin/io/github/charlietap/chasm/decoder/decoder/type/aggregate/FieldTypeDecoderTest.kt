package io.github.charlietap.chasm.decoder.decoder.type.aggregate

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.FieldType
import io.github.charlietap.chasm.ast.type.Mutability
import io.github.charlietap.chasm.ast.type.StorageType
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.fixture.ast.type.mutability
import io.github.charlietap.chasm.fixture.ast.type.storageType
import kotlin.test.Test
import kotlin.test.assertEquals

class FieldTypeDecoderTest {

    @Test
    fun `can decode an encoded field type`() {

        val reader = FakeWasmBinaryReader()
        val context = decoderContext(reader)

        val storageType = storageType()
        val storageTypeDecoder: Decoder<StorageType> = { _context ->
            assertEquals(context, _context)
            Ok(storageType)
        }

        val mutability = mutability()
        val mutabilityDecoder: Decoder<Mutability> = { _context ->
            assertEquals(context, _context)
            Ok(mutability)
        }

        val expected = Ok(FieldType(storageType, mutability))

        val actual = FieldTypeDecoder(
            context,
            storageTypeDecoder,
            mutabilityDecoder,
        )

        assertEquals(expected, actual)
    }
}
