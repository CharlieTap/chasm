package io.github.charlietap.chasm.decoder.wasm.decoder.type.aggregate

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.ArrayType
import io.github.charlietap.chasm.ast.type.FieldType
import io.github.charlietap.chasm.decoder.wasm.Decoder
import io.github.charlietap.chasm.decoder.wasm.fixture.decoderContext
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.fixture.type.fieldType
import kotlin.test.Test
import kotlin.test.assertEquals

class ArrayTypeDecoderTest {

    @Test
    fun `can decode an encoded array type`() {

        val reader = FakeWasmBinaryReader()
        val context = decoderContext(reader)

        val fieldType = fieldType()
        val fieldTypeDecoder: Decoder<FieldType> = { _context ->
            assertEquals(context, _context)
            Ok(fieldType)
        }

        val expected = Ok(ArrayType(fieldType))

        val actual = ArrayTypeDecoder(
            context,
            fieldTypeDecoder,
        )

        assertEquals(expected, actual)
    }
}
