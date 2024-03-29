package io.github.charlietap.chasm.decoder.wasm.decoder.type.aggregate

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.FieldType
import io.github.charlietap.chasm.ast.type.StructType
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.fixture.type.fieldType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class BinaryStructTypeDecoderTest {

    @Test
    fun `can decode an encoded struct type`() {

        val reader = FakeWasmBinaryReader()

        val fieldType = fieldType()
        val fieldTypeDecoder: FieldTypeDecoder = { _ ->
            fail("Field type decoder should not be called directly in this scenario")
        }

        val fieldTypes = listOf(fieldType)
        val vectorDecoder: VectorDecoder<FieldType> = { _reader, _subdecoder ->
            assertEquals(reader, _reader)
            assertEquals(fieldTypeDecoder, _subdecoder)
            Ok(Vector(fieldTypes))
        }

        val expected = Ok(StructType(fieldTypes))

        val actual = BinaryStructTypeDecoder(
            reader,
            fieldTypeDecoder,
            vectorDecoder,
        )

        assertEquals(expected, actual)
    }
}
