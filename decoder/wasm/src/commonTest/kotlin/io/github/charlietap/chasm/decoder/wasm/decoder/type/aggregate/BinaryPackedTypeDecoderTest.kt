package io.github.charlietap.chasm.decoder.wasm.decoder.type.aggregate

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.PackedType
import io.github.charlietap.chasm.decoder.wasm.error.TypeDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUByteReader
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryPackedTypeDecoderTest {

    @Test
    fun `can decode an encoded abstract heap type`() {

        val packedTypes = mapOf(
            PACKED_TYPE_I8 to PackedType.I8,
            PACKED_TYPE_I16 to PackedType.I16,
        )

        packedTypes.forEach { (packedTypeByte, packedType) ->
            val actual = BinaryPackedTypeDecoder(FakeUByteReader { Ok(packedTypeByte) })
            assertEquals(Ok(packedType), actual)
        }
    }

    @Test
    fun `throws an InvalidPackedType error when byte is unrecognised`() {

        val byte: UByte = 0x00u

        val actual = BinaryPackedTypeDecoder(FakeUByteReader { Ok(byte) })
        assertEquals(Err(TypeDecodeError.InvalidPackedType(byte)), actual)
    }
}
