package io.github.charlietap.chasm.decoder.type.reference

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.error.TypeDecodeError
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryReferenceTypeDecoderTest {

    @Test
    fun `can decode an encoded reference type`() {

        val referenceTypeMap = mapOf(
            0x70.toUByte() to Ok(ReferenceType.Funcref),
            0x6F.toUByte() to Ok(ReferenceType.Externref),
            0x11.toUByte() to Err(TypeDecodeError.InvalidReferenceType(0x11.toUByte())),
        )

        referenceTypeMap.forEach { (input, expected) ->
            val actual = BinaryReferenceTypeDecoder(input)
            assertEquals(expected, actual)
        }
    }
}
