package io.github.charlietap.chasm.decoder.decoder.type.aggregate

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.FieldType
import io.github.charlietap.chasm.ast.type.StructType
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.fixture.type.fieldType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class StructTypeDecoderTest {

    @Test
    fun `can decode an encoded struct type`() {

        val reader = FakeWasmBinaryReader()
        val context = decoderContext(reader)

        val fieldType = fieldType()
        val fieldTypeDecoder: Decoder<FieldType> = { _ ->
            fail("Field type decoder should not be called directly in this scenario")
        }

        val fieldTypes = listOf(fieldType)
        val vectorDecoder: VectorDecoder<FieldType> = { _context, _decoder ->
            assertEquals(context, _context)
            assertEquals(fieldTypeDecoder, _decoder)
            Ok(Vector(fieldTypes))
        }

        val expected = Ok(StructType(fieldTypes))

        val actual = StructTypeDecoder(
            context,
            fieldTypeDecoder,
            vectorDecoder,
        )

        assertEquals(expected, actual)
    }
}
