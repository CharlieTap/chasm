package io.github.charlietap.chasm.decoder.wasm.decoder.type.number

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.decoder.decoder.type.number.NumberTypeDecoder
import io.github.charlietap.chasm.decoder.error.TypeDecodeError
import io.github.charlietap.chasm.decoder.wasm.fixture.decoderContext
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUByteReader
import kotlin.test.Test
import kotlin.test.assertEquals

class NumberTypeDecoderTest {

    @Test
    fun `can decode an encoded number type`() {

        val numberTypeMap = mapOf(
            0x7F.toUByte() to Ok(NumberType.I32),
            0x7E.toUByte() to Ok(NumberType.I64),
            0x7D.toUByte() to Ok(NumberType.F32),
            0x7C.toUByte() to Ok(NumberType.F64),
            0x11.toUByte() to Err(TypeDecodeError.InvalidNumberType(0x11.toUByte())),
        )

        var byte: UByte = 0u
        val reader = FakeUByteReader { Ok(byte) }
        val context = decoderContext(reader)

        numberTypeMap.forEach { (input, expected) ->

            byte = input

            val actual = NumberTypeDecoder(context)
            assertEquals(expected, actual)
        }
    }
}
