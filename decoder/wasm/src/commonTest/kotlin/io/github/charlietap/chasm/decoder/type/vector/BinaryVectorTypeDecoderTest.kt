package io.github.charlietap.chasm.decoder.type.vector

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.VectorType
import io.github.charlietap.chasm.error.TypeDecodeError
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryVectorTypeDecoderTest {

    @Test
    fun `can decode an encoded vector type`() {
        val actual = BinaryVectorTypeDecoder(0x7B.toUByte())
        assertEquals(Ok(VectorType.V128), actual)
    }

    @Test
    fun `returns an error when the encoded byte doesnt match`() {
        val actual = BinaryVectorTypeDecoder(0x00.toUByte())
        assertEquals(Err(TypeDecodeError.InvalidVectorType(0x00.toUByte())), actual)
    }
}
