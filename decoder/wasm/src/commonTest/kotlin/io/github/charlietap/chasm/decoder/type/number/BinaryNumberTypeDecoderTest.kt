package io.github.charlietap.chasm.decoder.type.number

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.error.TypeDecodeError
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryNumberTypeDecoderTest {

    @Test
    fun `can decode an encoded number type`() {

        val numberTypeMap = mapOf(
            0x7F.toUByte() to Ok(NumberType.I32),
            0x7E.toUByte() to Ok(NumberType.I64),
            0x7D.toUByte() to Ok(NumberType.F32),
            0x7C.toUByte() to Ok(NumberType.F64),
            0x11.toUByte() to Err(TypeDecodeError.InvalidNumberType(0x11.toUByte())),
        )

        numberTypeMap.forEach { (input, expected) ->
            val actual = BinaryNumberTypeDecoder(input)
            assertEquals(expected, actual)
        }
    }
}
