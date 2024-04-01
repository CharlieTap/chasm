package io.github.charlietap.chasm.decoder.wasm.decoder.type.value

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.ast.type.VectorType
import io.github.charlietap.chasm.decoder.wasm.decoder.type.number.NumberTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.reference.ReferenceTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.vector.VECTOR_TYPE_128
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUByteReader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class BinaryValueTypeDecoderTest {

    @Test
    fun `delegates number type decoding to number type decoder`() {
        val numberTypes = NUMBER_TYPE_RANGE.map(UInt::toUByte)

        val referenceTypeDecoder: ReferenceTypeDecoder = { _, _ ->
            fail("ReferenceTypeDecoder should not be called in this scenario")
        }

        val expectedNumberType = NumberType.I32
        val expectedValueType = ValueType.Number(expectedNumberType)

        numberTypes.forEach { numberTypeByte ->

            val numberTypeDecoder: NumberTypeDecoder = { byte ->
                assertEquals(numberTypeByte, byte)

                Ok(expectedNumberType)
            }

            val actual = BinaryValueTypeDecoder(
                reader = FakeUByteReader { Ok(numberTypeByte) },
                numberTypeDecoder = numberTypeDecoder,
                referenceTypeDecoder = referenceTypeDecoder,
            )

            assertEquals(Ok(expectedValueType), actual)
        }
    }

    @Test
    fun `can decode an encoded vector type`() {

        val vectorTypeByte = VECTOR_TYPE_128

        val numberTypeDecoder: NumberTypeDecoder = { _ ->
            fail("NumberTypeDecoder should not be called in this scenario")
        }

        val referenceTypeDecoder: ReferenceTypeDecoder = { _, _ ->
            fail("ReferenceTypeDecoder should not be called in this scenario")
        }

        val expectedValueType = ValueType.Vector(VectorType.V128)

        val actual = BinaryValueTypeDecoder(
            reader = FakeUByteReader { Ok(vectorTypeByte) },
            numberTypeDecoder = numberTypeDecoder,
            referenceTypeDecoder = referenceTypeDecoder,
        )

        assertEquals(Ok(expectedValueType), actual)
    }

    @Test
    fun `delegates reference type decoding to reference type decoder`() {
        val referenceTypes = REFERENCE_TYPE_RANGE.map(UInt::toUByte)

        val numberTypeDecoder: NumberTypeDecoder = { _ ->
            fail("NumberTypeDecoder should not be called in this scenario")
        }

        val expectedReferenceType = ReferenceType.RefNull(AbstractHeapType.Func)
        val expectedValueType = ValueType.Reference(expectedReferenceType)

        referenceTypes.forEach { referenceTypeByte ->

            val reader = FakeUByteReader { Ok(referenceTypeByte) }

            val referenceTypeDecoder: ReferenceTypeDecoder = { _reader, _byte ->
                assertEquals(reader, _reader)
                assertEquals(referenceTypeByte, _byte)

                Ok(expectedReferenceType)
            }

            val actual = BinaryValueTypeDecoder(
                reader = reader,
                numberTypeDecoder = numberTypeDecoder,
                referenceTypeDecoder = referenceTypeDecoder,
            )

            assertEquals(Ok(expectedValueType), actual)
        }
    }
}
