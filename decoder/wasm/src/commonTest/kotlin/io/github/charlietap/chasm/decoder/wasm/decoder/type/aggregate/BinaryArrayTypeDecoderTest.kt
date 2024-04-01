package io.github.charlietap.chasm.decoder.wasm.decoder.type.aggregate

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.ArrayType
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.fixture.type.fieldType
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryArrayTypeDecoderTest {

    @Test
    fun `can decode an encoded array type`() {

        val reader = FakeWasmBinaryReader()

        val fieldType = fieldType()
        val fieldTypeDecoder: FieldTypeDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(fieldType)
        }

        val expected = Ok(ArrayType(fieldType))

        val actual = BinaryArrayTypeDecoder(
            reader,
            fieldTypeDecoder,
        )

        assertEquals(expected, actual)
    }
}
