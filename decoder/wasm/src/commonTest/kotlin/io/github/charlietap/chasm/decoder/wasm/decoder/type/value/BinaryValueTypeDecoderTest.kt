package io.github.charlietap.chasm.decoder.wasm.decoder.type.value

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.ast.type.VectorType
import io.github.charlietap.chasm.decoder.wasm.decoder.type.reference.ReferenceTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.error.TypeDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUByteReader
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryValueTypeDecoderTest {

    @Test
    fun `can decode an encoded value type`() {

        val numberTypeMap = mapOf(
            0x7F.toUByte() to Ok(ValueType.Number(NumberType.I32)),
            0x7E.toUByte() to Ok(ValueType.Number(NumberType.I64)),
            0x7D.toUByte() to Ok(ValueType.Number(NumberType.F32)),
            0x7C.toUByte() to Ok(ValueType.Number(NumberType.F64)),
            0x7B.toUByte() to Ok(ValueType.Vector(VectorType.V128)),
            0x70.toUByte() to Ok(ValueType.Reference(ReferenceType.RefNull(HeapType.Func))),
            0x6F.toUByte() to Ok(ValueType.Reference(ReferenceType.RefNull(HeapType.Extern))),
            0x63.toUByte() to Ok(ValueType.Reference(ReferenceType.RefNull(HeapType.Extern))),
            0x64.toUByte() to Ok(ValueType.Reference(ReferenceType.Ref(HeapType.Extern))),
            0x00.toUByte() to Err(TypeDecodeError.InvalidValueType(0x00.toUByte())),
        )

        val referenceTypeDecoder: ReferenceTypeDecoder = { _, _opcode ->
            if (_opcode == VALUE_TYPE_REFERENCE_REF) {
                Ok(ReferenceType.Ref(HeapType.Extern))
            } else {
                Ok(ReferenceType.RefNull(HeapType.Extern))
            }
        }

        numberTypeMap.forEach { (input, expected) ->
            val reader = FakeUByteReader { Ok(input) }
            val actual = BinaryValueTypeDecoder(reader, referenceTypeDecoder)
            assertEquals(expected, actual)
        }
    }
}
