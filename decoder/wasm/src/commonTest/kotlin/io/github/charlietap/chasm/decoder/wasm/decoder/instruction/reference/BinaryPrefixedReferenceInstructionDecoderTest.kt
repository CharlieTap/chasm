package io.github.charlietap.chasm.decoder.wasm.decoder.instruction.reference

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.DataIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.ElementIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.FieldIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.TypeIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.heap.HeapTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUIntReader
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.fixture.module.dataIndex
import io.github.charlietap.chasm.fixture.module.elementIndex
import io.github.charlietap.chasm.fixture.module.fieldIndex
import io.github.charlietap.chasm.fixture.module.typeIndex
import io.github.charlietap.chasm.fixture.type.heapType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class BinaryPrefixedReferenceInstructionDecoderTest {

    @Test
    fun `can decode an encoded struct new instruction`() {

        val reader = FakeUIntReader { Ok(STRUCT_NEW) }

        val typeIndex = typeIndex()
        val typeIndexDecoder: TypeIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(typeIndex)
        }

        val expected = AggregateInstruction.StructNew(typeIndex)

        val actual = BinaryPrefixedReferenceInstructionDecoder(
            reader = reader,
            dataIndexDecoder = dataIndexDecoder(),
            elementIndexDecoder = elementIndexDecoder(),
            fieldIndexDecoder = fieldIndexDecoder(),
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = heapTypeDecoder(),
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded struct new default instruction`() {

        val reader = FakeUIntReader { Ok(STRUCT_NEW_DEFAULT) }

        val typeIndex = typeIndex()
        val typeIndexDecoder: TypeIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(typeIndex)
        }

        val expected = AggregateInstruction.StructNewDefault(typeIndex)

        val actual = BinaryPrefixedReferenceInstructionDecoder(
            reader = reader,
            dataIndexDecoder = dataIndexDecoder(),
            elementIndexDecoder = elementIndexDecoder(),
            fieldIndexDecoder = fieldIndexDecoder(),
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = heapTypeDecoder(),
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded struct get instruction`() {

        val reader = FakeUIntReader { Ok(STRUCT_GET) }

        val typeIndex = typeIndex()
        val typeIndexDecoder: TypeIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(typeIndex)
        }

        val fieldIndex = fieldIndex()
        val fieldIndexDecoder: FieldIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(fieldIndex)
        }

        val expected = AggregateInstruction.StructGet(typeIndex, fieldIndex)

        val actual = BinaryPrefixedReferenceInstructionDecoder(
            reader = reader,
            dataIndexDecoder = dataIndexDecoder(),
            elementIndexDecoder = elementIndexDecoder(),
            fieldIndexDecoder = fieldIndexDecoder,
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = heapTypeDecoder(),
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded struct get signed instruction`() {

        val reader = FakeUIntReader { Ok(STRUCT_GET_SIGNED) }

        val typeIndex = typeIndex()
        val typeIndexDecoder: TypeIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(typeIndex)
        }

        val fieldIndex = fieldIndex()
        val fieldIndexDecoder: FieldIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(fieldIndex)
        }

        val expected = AggregateInstruction.StructGetSigned(typeIndex, fieldIndex)

        val actual = BinaryPrefixedReferenceInstructionDecoder(
            reader = reader,
            dataIndexDecoder = dataIndexDecoder(),
            elementIndexDecoder = elementIndexDecoder(),
            fieldIndexDecoder = fieldIndexDecoder,
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = heapTypeDecoder(),
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded struct get unsigned instruction`() {

        val reader = FakeUIntReader { Ok(STRUCT_GET_UNSIGNED) }

        val typeIndex = typeIndex()
        val typeIndexDecoder: TypeIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(typeIndex)
        }

        val fieldIndex = fieldIndex()
        val fieldIndexDecoder: FieldIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(fieldIndex)
        }

        val expected = AggregateInstruction.StructGetUnsigned(typeIndex, fieldIndex)

        val actual = BinaryPrefixedReferenceInstructionDecoder(
            reader = reader,
            dataIndexDecoder = dataIndexDecoder(),
            elementIndexDecoder = elementIndexDecoder(),
            fieldIndexDecoder = fieldIndexDecoder,
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = heapTypeDecoder(),
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded struct set instruction`() {

        val reader = FakeUIntReader { Ok(STRUCT_SET) }

        val typeIndex = typeIndex()
        val typeIndexDecoder: TypeIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(typeIndex)
        }

        val fieldIndex = fieldIndex()
        val fieldIndexDecoder: FieldIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(fieldIndex)
        }

        val expected = AggregateInstruction.StructSet(typeIndex, fieldIndex)

        val actual = BinaryPrefixedReferenceInstructionDecoder(
            reader = reader,
            dataIndexDecoder = dataIndexDecoder(),
            elementIndexDecoder = elementIndexDecoder(),
            fieldIndexDecoder = fieldIndexDecoder,
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = heapTypeDecoder(),
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded array new instruction`() {

        val reader = FakeUIntReader { Ok(ARRAY_NEW) }

        val typeIndex = typeIndex()
        val typeIndexDecoder: TypeIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(typeIndex)
        }

        val expected = AggregateInstruction.ArrayNew(typeIndex)

        val actual = BinaryPrefixedReferenceInstructionDecoder(
            reader = reader,
            dataIndexDecoder = dataIndexDecoder(),
            elementIndexDecoder = elementIndexDecoder(),
            fieldIndexDecoder = fieldIndexDecoder(),
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = heapTypeDecoder(),
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded array new default instruction`() {

        val reader = FakeUIntReader { Ok(ARRAY_NEW_DEFAULT) }

        val typeIndex = typeIndex()
        val typeIndexDecoder: TypeIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(typeIndex)
        }

        val expected = AggregateInstruction.ArrayNewDefault(typeIndex)

        val actual = BinaryPrefixedReferenceInstructionDecoder(
            reader = reader,
            dataIndexDecoder = dataIndexDecoder(),
            elementIndexDecoder = elementIndexDecoder(),
            fieldIndexDecoder = fieldIndexDecoder(),
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = heapTypeDecoder(),
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded array new fixed instruction`() {

        val opcode = ARRAY_NEW_FIXED
        val size = 117u
        val uInts = sequenceOf(opcode, size).iterator()
        val reader = FakeWasmBinaryReader(
            fakeUIntReader = { Ok(uInts.next()) },
        )

        val typeIndex = typeIndex()
        val typeIndexDecoder: TypeIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(typeIndex)
        }

        val expected = AggregateInstruction.ArrayNewFixed(typeIndex, size)

        val actual = BinaryPrefixedReferenceInstructionDecoder(
            reader = reader,
            dataIndexDecoder = dataIndexDecoder(),
            elementIndexDecoder = elementIndexDecoder(),
            fieldIndexDecoder = fieldIndexDecoder(),
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = heapTypeDecoder(),
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded array new data instruction`() {

        val reader = FakeUIntReader { Ok(ARRAY_NEW_DATA) }

        val typeIndex = typeIndex()
        val typeIndexDecoder: TypeIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(typeIndex)
        }

        val dataIndex = dataIndex()
        val dataIndexDecoder: DataIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(dataIndex)
        }

        val expected = AggregateInstruction.ArrayNewData(typeIndex, dataIndex)

        val actual = BinaryPrefixedReferenceInstructionDecoder(
            reader = reader,
            dataIndexDecoder = dataIndexDecoder,
            elementIndexDecoder = elementIndexDecoder(),
            fieldIndexDecoder = fieldIndexDecoder(),
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = heapTypeDecoder(),
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded array new element instruction`() {

        val reader = FakeUIntReader { Ok(ARRAY_NEW_ELEMENT) }

        val typeIndex = typeIndex()
        val typeIndexDecoder: TypeIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(typeIndex)
        }

        val elementIndex = elementIndex()
        val elementIndexDecoder: ElementIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(elementIndex)
        }

        val expected = AggregateInstruction.ArrayNewElement(typeIndex, elementIndex)

        val actual = BinaryPrefixedReferenceInstructionDecoder(
            reader = reader,
            dataIndexDecoder = dataIndexDecoder(),
            elementIndexDecoder = elementIndexDecoder,
            fieldIndexDecoder = fieldIndexDecoder(),
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = heapTypeDecoder(),
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded array get instruction`() {

        val reader = FakeUIntReader { Ok(ARRAY_GET) }

        val typeIndex = typeIndex()
        val typeIndexDecoder: TypeIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(typeIndex)
        }

        val expected = AggregateInstruction.ArrayGet(typeIndex)

        val actual = BinaryPrefixedReferenceInstructionDecoder(
            reader = reader,
            dataIndexDecoder = dataIndexDecoder(),
            elementIndexDecoder = elementIndexDecoder(),
            fieldIndexDecoder = fieldIndexDecoder(),
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = heapTypeDecoder(),
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded array get signed instruction`() {

        val reader = FakeUIntReader { Ok(ARRAY_GET_SIGNED) }

        val typeIndex = typeIndex()
        val typeIndexDecoder: TypeIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(typeIndex)
        }

        val expected = AggregateInstruction.ArrayGetSigned(typeIndex)

        val actual = BinaryPrefixedReferenceInstructionDecoder(
            reader = reader,
            dataIndexDecoder = dataIndexDecoder(),
            elementIndexDecoder = elementIndexDecoder(),
            fieldIndexDecoder = fieldIndexDecoder(),
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = heapTypeDecoder(),
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded array get unsigned instruction`() {

        val reader = FakeUIntReader { Ok(ARRAY_GET_UNSIGNED) }

        val typeIndex = typeIndex()
        val typeIndexDecoder: TypeIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(typeIndex)
        }

        val expected = AggregateInstruction.ArrayGetUnsigned(typeIndex)

        val actual = BinaryPrefixedReferenceInstructionDecoder(
            reader = reader,
            dataIndexDecoder = dataIndexDecoder(),
            elementIndexDecoder = elementIndexDecoder(),
            fieldIndexDecoder = fieldIndexDecoder(),
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = heapTypeDecoder(),
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded array set instruction`() {

        val reader = FakeUIntReader { Ok(ARRAY_SET) }

        val typeIndex = typeIndex()
        val typeIndexDecoder: TypeIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(typeIndex)
        }

        val expected = AggregateInstruction.ArraySet(typeIndex)

        val actual = BinaryPrefixedReferenceInstructionDecoder(
            reader = reader,
            dataIndexDecoder = dataIndexDecoder(),
            elementIndexDecoder = elementIndexDecoder(),
            fieldIndexDecoder = fieldIndexDecoder(),
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = heapTypeDecoder(),
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded array len instruction`() {

        val reader = FakeUIntReader { Ok(ARRAY_LEN) }

        val typeIndex = typeIndex()
        val typeIndexDecoder: TypeIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(typeIndex)
        }

        val expected = AggregateInstruction.ArrayLen

        val actual = BinaryPrefixedReferenceInstructionDecoder(
            reader = reader,
            dataIndexDecoder = dataIndexDecoder(),
            elementIndexDecoder = elementIndexDecoder(),
            fieldIndexDecoder = fieldIndexDecoder(),
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = heapTypeDecoder(),
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded array fill instruction`() {

        val reader = FakeUIntReader { Ok(ARRAY_FILL) }

        val typeIndex = typeIndex()
        val typeIndexDecoder: TypeIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(typeIndex)
        }

        val expected = AggregateInstruction.ArrayFill(typeIndex)

        val actual = BinaryPrefixedReferenceInstructionDecoder(
            reader = reader,
            dataIndexDecoder = dataIndexDecoder(),
            elementIndexDecoder = elementIndexDecoder(),
            fieldIndexDecoder = fieldIndexDecoder(),
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = heapTypeDecoder(),
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded array copy instruction`() {

        val reader = FakeUIntReader { Ok(ARRAY_COPY) }

        val typeIndex = typeIndex()
        val typeIndexDecoder: TypeIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(typeIndex)
        }

        val expected = AggregateInstruction.ArrayCopy(typeIndex, typeIndex)

        val actual = BinaryPrefixedReferenceInstructionDecoder(
            reader = reader,
            dataIndexDecoder = dataIndexDecoder(),
            elementIndexDecoder = elementIndexDecoder(),
            fieldIndexDecoder = fieldIndexDecoder(),
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = heapTypeDecoder(),
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded array init data instruction`() {

        val reader = FakeUIntReader { Ok(ARRAY_INIT_DATA) }

        val typeIndex = typeIndex()
        val typeIndexDecoder: TypeIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(typeIndex)
        }

        val dataIndex = dataIndex()
        val dataIndexDecoder: DataIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(dataIndex)
        }

        val expected = AggregateInstruction.ArrayInitData(typeIndex, dataIndex)

        val actual = BinaryPrefixedReferenceInstructionDecoder(
            reader = reader,
            dataIndexDecoder = dataIndexDecoder,
            elementIndexDecoder = elementIndexDecoder(),
            fieldIndexDecoder = fieldIndexDecoder(),
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = heapTypeDecoder(),
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded array init element instruction`() {

        val reader = FakeUIntReader { Ok(ARRAY_INIT_ELEMENT) }

        val typeIndex = typeIndex()
        val typeIndexDecoder: TypeIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(typeIndex)
        }

        val elementIndex = elementIndex()
        val elementIndexDecoder: ElementIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(elementIndex)
        }

        val expected = AggregateInstruction.ArrayInitElement(typeIndex, elementIndex)

        val actual = BinaryPrefixedReferenceInstructionDecoder(
            reader = reader,
            dataIndexDecoder = dataIndexDecoder(),
            elementIndexDecoder = elementIndexDecoder,
            fieldIndexDecoder = fieldIndexDecoder(),
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = heapTypeDecoder(),
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded ref test instruction`() {

        val reader = FakeUIntReader { Ok(REF_TEST) }

        val heapType = heapType()
        val heapTypeDecoder: HeapTypeDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(heapType)
        }

        val expected = ReferenceInstruction.RefTest(
            ReferenceType.Ref(heapType),
        )

        val actual = BinaryPrefixedReferenceInstructionDecoder(
            reader = reader,
            dataIndexDecoder = dataIndexDecoder(),
            elementIndexDecoder = elementIndexDecoder(),
            fieldIndexDecoder = fieldIndexDecoder(),
            typeIndexDecoder = typeIndexDecoder(),
            heapTypeDecoder = heapTypeDecoder,
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded ref test instruction with a null ref`() {

        val reader = FakeUIntReader { Ok(REF_TEST_NULL) }

        val heapType = heapType()
        val heapTypeDecoder: HeapTypeDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(heapType)
        }

        val expected = ReferenceInstruction.RefTest(
            ReferenceType.RefNull(heapType),
        )

        val actual = BinaryPrefixedReferenceInstructionDecoder(
            reader = reader,
            dataIndexDecoder = dataIndexDecoder(),
            elementIndexDecoder = elementIndexDecoder(),
            fieldIndexDecoder = fieldIndexDecoder(),
            typeIndexDecoder = typeIndexDecoder(),
            heapTypeDecoder = heapTypeDecoder,
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded ref cast instruction`() {

        val reader = FakeUIntReader { Ok(REF_CAST) }

        val heapType = heapType()
        val heapTypeDecoder: HeapTypeDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(heapType)
        }

        val expected = ReferenceInstruction.RefCast(
            ReferenceType.Ref(heapType),
        )

        val actual = BinaryPrefixedReferenceInstructionDecoder(
            reader = reader,
            dataIndexDecoder = dataIndexDecoder(),
            elementIndexDecoder = elementIndexDecoder(),
            fieldIndexDecoder = fieldIndexDecoder(),
            typeIndexDecoder = typeIndexDecoder(),
            heapTypeDecoder = heapTypeDecoder,
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded ref cast instruction with a null ref`() {

        val reader = FakeUIntReader { Ok(REF_CAST_NULL) }

        val heapType = heapType()
        val heapTypeDecoder: HeapTypeDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(heapType)
        }

        val expected = ReferenceInstruction.RefCast(
            ReferenceType.RefNull(heapType),
        )

        val actual = BinaryPrefixedReferenceInstructionDecoder(
            reader = reader,
            dataIndexDecoder = dataIndexDecoder(),
            elementIndexDecoder = elementIndexDecoder(),
            fieldIndexDecoder = fieldIndexDecoder(),
            typeIndexDecoder = typeIndexDecoder(),
            heapTypeDecoder = heapTypeDecoder,
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded aggregate instruction`() {

        val aggregateInstructionMap = mapOf(
            ANY_CONVERT_EXTERN to AggregateInstruction.AnyConvertExtern,
            EXTERN_CONVERT_ANY to AggregateInstruction.ExternConvertAny,
            REF_I31 to AggregateInstruction.RefI31,
            I31_GET_SIGNED to AggregateInstruction.I31GetSigned,
            I31_GET_UNSIGNED to AggregateInstruction.I31GetUnsigned,
        )

        aggregateInstructionMap.forEach { (instructionOp, expected) ->
            val reader = FakeUIntReader { Ok(instructionOp) }

            val actual = BinaryPrefixedReferenceInstructionDecoder(
                reader = reader,
                dataIndexDecoder = dataIndexDecoder(),
                elementIndexDecoder = elementIndexDecoder(),
                fieldIndexDecoder = fieldIndexDecoder(),
                typeIndexDecoder = typeIndexDecoder(),
                heapTypeDecoder = heapTypeDecoder(),
            )

            assertEquals(Ok(expected), actual)
        }
    }

    companion object {
        private fun dataIndexDecoder(): DataIndexDecoder = { _ ->
            fail("DataIndexDecoder should not be called in this scenario")
        }

        private fun elementIndexDecoder(): ElementIndexDecoder = { _ ->
            fail("ElementIndexDecoder should not be called in this scenario")
        }

        private fun fieldIndexDecoder(): FieldIndexDecoder = { _ ->
            fail("FieldIndexDecoder should not be called in this scenario")
        }

        private fun typeIndexDecoder(): TypeIndexDecoder = { _ ->
            fail("TypeIndexDecoder should not be called in this scenario")
        }

        private fun heapTypeDecoder(): HeapTypeDecoder = { _ ->
            fail("HeapTypeDecoder should not be called in this scenario")
        }
    }
}
