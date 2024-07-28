package io.github.charlietap.chasm.decoder.wasm.decoder.type.value

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.ast.type.VectorType
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.type.value.NUMBER_TYPE_RANGE
import io.github.charlietap.chasm.decoder.decoder.type.value.REFERENCE_TYPE_RANGE
import io.github.charlietap.chasm.decoder.decoder.type.value.ValueTypeDecoder
import io.github.charlietap.chasm.decoder.decoder.type.vector.VECTOR_TYPE_128
import io.github.charlietap.chasm.decoder.wasm.fixture.decoderContext
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUByteReader
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ValueTypeDecoderTest {

    @Test
    fun `delegates number type decoding to number type decoder`() {

        var byte: UByte = 0u
        val peekReader = FakeUByteReader { Ok(byte) }
        val reader = FakeWasmBinaryReader(fakePeekReader = { peekReader })
        val context = decoderContext(reader)

        val numberTypes = NUMBER_TYPE_RANGE.map(UInt::toUByte)

        val referenceTypeDecoder: Decoder<ReferenceType> = { _ ->
            fail("ReferenceTypeDecoder should not be called in this scenario")
        }

        val expectedNumberType = NumberType.I32
        val expectedValueType = ValueType.Number(expectedNumberType)

        numberTypes.forEach { numberTypeByte ->

            byte = numberTypeByte

            val numberTypeDecoder: Decoder<NumberType> = { _context ->
                assertEquals(context, _context)

                Ok(expectedNumberType)
            }

            val actual = ValueTypeDecoder(
                context = context,
                numberTypeDecoder = numberTypeDecoder,
                referenceTypeDecoder = referenceTypeDecoder,
            )

            assertEquals(Ok(expectedValueType), actual)
        }
    }

    @Test
    fun `can decode an encoded vector type`() {

        val vectorTypeByte = VECTOR_TYPE_128

        val peekReader = FakeUByteReader { Ok(vectorTypeByte) }
        val reader = FakeWasmBinaryReader(
            fakeUByteReader = { Ok(vectorTypeByte) },
            fakePeekReader = { peekReader },
        )
        val context = decoderContext(reader)

        val numberTypeDecoder: Decoder<NumberType> = { _ ->
            fail("NumberTypeDecoder should not be called in this scenario")
        }

        val referenceTypeDecoder: Decoder<ReferenceType> = { _ ->
            fail("ReferenceTypeDecoder should not be called in this scenario")
        }

        val expectedValueType = ValueType.Vector(VectorType.V128)

        val actual = ValueTypeDecoder(
            context = context,
            numberTypeDecoder = numberTypeDecoder,
            referenceTypeDecoder = referenceTypeDecoder,
        )

        assertEquals(Ok(expectedValueType), actual)
    }

    @Test
    fun `delegates reference type decoding to reference type decoder`() {

        val referenceTypes = REFERENCE_TYPE_RANGE.map(UInt::toUByte)

        val numberTypeDecoder: Decoder<NumberType> = { _ ->
            fail("NumberTypeDecoder should not be called in this scenario")
        }

        val expectedReferenceType = ReferenceType.RefNull(AbstractHeapType.Func)
        val expectedValueType = ValueType.Reference(expectedReferenceType)

        var byte: UByte = 0u
        val peekReader = FakeUByteReader { Ok(byte) }
        val reader = FakeWasmBinaryReader(fakePeekReader = { peekReader })
        val context = decoderContext(reader)

        referenceTypes.forEach { referenceTypeByte ->

            byte = referenceTypeByte

            val referenceTypeDecoder: Decoder<ReferenceType> = { _context ->
                assertEquals(context, _context)

                Ok(expectedReferenceType)
            }

            val actual = ValueTypeDecoder(
                context = context,
                numberTypeDecoder = numberTypeDecoder,
                referenceTypeDecoder = referenceTypeDecoder,
            )

            assertEquals(Ok(expectedValueType), actual)
        }
    }
}
