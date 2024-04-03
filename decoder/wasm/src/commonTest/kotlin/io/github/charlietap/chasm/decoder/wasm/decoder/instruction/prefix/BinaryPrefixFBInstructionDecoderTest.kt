package io.github.charlietap.chasm.decoder.wasm.decoder.instruction.prefix

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.PREFIX_FB
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.control.CastFlags
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.control.CastFlagsDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.control.Nullability
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.DataIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.ElementIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.FieldIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.LabelIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.TypeIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.heap.HeapTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUIntReader
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.fixture.module.dataIndex
import io.github.charlietap.chasm.fixture.module.elementIndex
import io.github.charlietap.chasm.fixture.module.fieldIndex
import io.github.charlietap.chasm.fixture.module.labelIndex
import io.github.charlietap.chasm.fixture.module.typeIndex
import io.github.charlietap.chasm.fixture.type.heapType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class BinaryPrefixFBInstructionDecoderTest {

    @Test
    fun `can decode an encoded br on cast instruction`() {

        val opcode = BR_ON_CAST
        val reader = FakeWasmBinaryReader()

        val labelIndex = labelIndex()
        val labelIndexDecoder: LabelIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(labelIndex)
        }

        val castFlags = CastFlags(Nullability.NULL, Nullability.NON_NULL)
        val castFlagsDecoder: CastFlagsDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(castFlags)
        }

        val heapType = heapType()
        val heapTypeDecoder: HeapTypeDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(heapType)
        }

        val expected = ControlInstruction.BrOnCast(
            labelIndex,
            ReferenceType.RefNull(heapType),
            ReferenceType.Ref(heapType),
        )

        val actual = BinaryPrefixFBInstructionDecoder(
            reader = reader,
            opcode = opcode,
            dataIndexDecoder = neverDataIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            fieldIndexDecoder = neverFieldIndexDecoder,
            typeIndexDecoder = neverTypeIndexDecoder,
            heapTypeDecoder = heapTypeDecoder,
            labelIndexDecoder = labelIndexDecoder,
            castFlagsDecoder = castFlagsDecoder,
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded br on cast fail instruction`() {

        val opcode = BR_ON_CAST_FAIL
        val reader = FakeWasmBinaryReader()

        val labelIndex = labelIndex()
        val labelIndexDecoder: LabelIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(labelIndex)
        }

        val castFlags = CastFlags(Nullability.NULL, Nullability.NON_NULL)
        val castFlagsDecoder: CastFlagsDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(castFlags)
        }

        val heapType = heapType()
        val heapTypeDecoder: HeapTypeDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(heapType)
        }

        val expected = ControlInstruction.BrOnCastFail(
            labelIndex,
            ReferenceType.RefNull(heapType),
            ReferenceType.Ref(heapType),
        )

        val actual = BinaryPrefixFBInstructionDecoder(
            reader = reader,
            opcode = opcode,
            dataIndexDecoder = neverDataIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            fieldIndexDecoder = neverFieldIndexDecoder,
            typeIndexDecoder = neverTypeIndexDecoder,
            heapTypeDecoder = heapTypeDecoder,
            labelIndexDecoder = labelIndexDecoder,
            castFlagsDecoder = castFlagsDecoder,
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `returns an unknown prefix instruction error when the opcode doesn't match`() {

        val prefix = PREFIX_FB
        val opcode = 117u

        val expected = Err(InstructionDecodeError.InvalidPrefixInstruction(prefix, opcode))

        val reader = FakeWasmBinaryReader()
        val actual = BinaryPrefixFBInstructionDecoder(reader, opcode)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an encoded struct new instruction`() {

        val opcode = STRUCT_NEW
        val reader = FakeWasmBinaryReader()

        val typeIndex = typeIndex()
        val typeIndexDecoder: TypeIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(typeIndex)
        }

        val expected = AggregateInstruction.StructNew(typeIndex)

        val actual = BinaryPrefixFBInstructionDecoder(
            reader = reader,
            opcode = opcode,
            dataIndexDecoder = neverDataIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            fieldIndexDecoder = neverFieldIndexDecoder,
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = neverHeapTypeDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            castFlagsDecoder = neverCastFlagsDecoder,
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded struct new default instruction`() {

        val opcode = STRUCT_NEW_DEFAULT
        val reader = FakeWasmBinaryReader()

        val typeIndex = typeIndex()
        val typeIndexDecoder: TypeIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(typeIndex)
        }

        val expected = AggregateInstruction.StructNewDefault(typeIndex)

        val actual = BinaryPrefixFBInstructionDecoder(
            reader = reader,
            opcode = opcode,
            dataIndexDecoder = neverDataIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            fieldIndexDecoder = neverFieldIndexDecoder,
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = neverHeapTypeDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            castFlagsDecoder = neverCastFlagsDecoder,
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded struct get instruction`() {

        val opcode = STRUCT_GET
        val reader = FakeWasmBinaryReader()

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

        val actual = BinaryPrefixFBInstructionDecoder(
            reader = reader,
            opcode = opcode,
            dataIndexDecoder = neverDataIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            fieldIndexDecoder = fieldIndexDecoder,
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = neverHeapTypeDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            castFlagsDecoder = neverCastFlagsDecoder,
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded struct get signed instruction`() {

        val opcode = STRUCT_GET_SIGNED
        val reader = FakeWasmBinaryReader()

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

        val actual = BinaryPrefixFBInstructionDecoder(
            reader = reader,
            opcode = opcode,
            dataIndexDecoder = neverDataIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            fieldIndexDecoder = fieldIndexDecoder,
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = neverHeapTypeDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            castFlagsDecoder = neverCastFlagsDecoder,
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded struct get unsigned instruction`() {

        val opcode = STRUCT_GET_UNSIGNED
        val reader = FakeWasmBinaryReader()

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

        val actual = BinaryPrefixFBInstructionDecoder(
            reader = reader,
            opcode = opcode,
            dataIndexDecoder = neverDataIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            fieldIndexDecoder = fieldIndexDecoder,
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = neverHeapTypeDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            castFlagsDecoder = neverCastFlagsDecoder,
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded struct set instruction`() {

        val opcode = STRUCT_SET
        val reader = FakeWasmBinaryReader()

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

        val actual = BinaryPrefixFBInstructionDecoder(
            reader = reader,
            opcode = opcode,
            dataIndexDecoder = neverDataIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            fieldIndexDecoder = fieldIndexDecoder,
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = neverHeapTypeDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            castFlagsDecoder = neverCastFlagsDecoder,
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded array new instruction`() {

        val opcode = ARRAY_NEW
        val reader = FakeWasmBinaryReader()

        val typeIndex = typeIndex()
        val typeIndexDecoder: TypeIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(typeIndex)
        }

        val expected = AggregateInstruction.ArrayNew(typeIndex)

        val actual = BinaryPrefixFBInstructionDecoder(
            reader = reader,
            opcode = opcode,
            dataIndexDecoder = neverDataIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            fieldIndexDecoder = neverFieldIndexDecoder,
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = neverHeapTypeDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            castFlagsDecoder = neverCastFlagsDecoder,
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded array new default instruction`() {

        val opcode = ARRAY_NEW_DEFAULT
        val reader = FakeWasmBinaryReader()

        val typeIndex = typeIndex()
        val typeIndexDecoder: TypeIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(typeIndex)
        }

        val expected = AggregateInstruction.ArrayNewDefault(typeIndex)

        val actual = BinaryPrefixFBInstructionDecoder(
            reader = reader,
            opcode = opcode,
            dataIndexDecoder = neverDataIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            fieldIndexDecoder = neverFieldIndexDecoder,
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = neverHeapTypeDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            castFlagsDecoder = neverCastFlagsDecoder,
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded array new fixed instruction`() {

        val opcode = ARRAY_NEW_FIXED
        val size = 117u
        val reader = FakeUIntReader { Ok(size) }

        val typeIndex = typeIndex()
        val typeIndexDecoder: TypeIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(typeIndex)
        }

        val expected = AggregateInstruction.ArrayNewFixed(typeIndex, size)

        val actual = BinaryPrefixFBInstructionDecoder(
            reader = reader,
            opcode = opcode,
            dataIndexDecoder = neverDataIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            fieldIndexDecoder = neverFieldIndexDecoder,
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = neverHeapTypeDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            castFlagsDecoder = neverCastFlagsDecoder,
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded array new data instruction`() {

        val opcode = ARRAY_NEW_DATA
        val reader = FakeWasmBinaryReader()

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

        val actual = BinaryPrefixFBInstructionDecoder(
            reader = reader,
            opcode = opcode,
            dataIndexDecoder = dataIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            fieldIndexDecoder = neverFieldIndexDecoder,
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = neverHeapTypeDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            castFlagsDecoder = neverCastFlagsDecoder,
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded array new element instruction`() {

        val opcode = ARRAY_NEW_ELEMENT
        val reader = FakeWasmBinaryReader()

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

        val actual = BinaryPrefixFBInstructionDecoder(
            reader = reader,
            opcode = opcode,
            dataIndexDecoder = neverDataIndexDecoder,
            elementIndexDecoder = elementIndexDecoder,
            fieldIndexDecoder = neverFieldIndexDecoder,
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = neverHeapTypeDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            castFlagsDecoder = neverCastFlagsDecoder,
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded array get instruction`() {

        val opcode = ARRAY_GET
        val reader = FakeWasmBinaryReader()

        val typeIndex = typeIndex()
        val typeIndexDecoder: TypeIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(typeIndex)
        }

        val expected = AggregateInstruction.ArrayGet(typeIndex)

        val actual = BinaryPrefixFBInstructionDecoder(
            reader = reader,
            opcode = opcode,
            dataIndexDecoder = neverDataIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            fieldIndexDecoder = neverFieldIndexDecoder,
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = neverHeapTypeDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            castFlagsDecoder = neverCastFlagsDecoder,
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded array get signed instruction`() {

        val opcode = ARRAY_GET_SIGNED
        val reader = FakeWasmBinaryReader()

        val typeIndex = typeIndex()
        val typeIndexDecoder: TypeIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(typeIndex)
        }

        val expected = AggregateInstruction.ArrayGetSigned(typeIndex)

        val actual = BinaryPrefixFBInstructionDecoder(
            reader = reader,
            opcode = opcode,
            dataIndexDecoder = neverDataIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            fieldIndexDecoder = neverFieldIndexDecoder,
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = neverHeapTypeDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            castFlagsDecoder = neverCastFlagsDecoder,
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded array get unsigned instruction`() {

        val opcode = ARRAY_GET_UNSIGNED
        val reader = FakeWasmBinaryReader()

        val typeIndex = typeIndex()
        val typeIndexDecoder: TypeIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(typeIndex)
        }

        val expected = AggregateInstruction.ArrayGetUnsigned(typeIndex)

        val actual = BinaryPrefixFBInstructionDecoder(
            reader = reader,
            opcode = opcode,
            dataIndexDecoder = neverDataIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            fieldIndexDecoder = neverFieldIndexDecoder,
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = neverHeapTypeDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            castFlagsDecoder = neverCastFlagsDecoder,
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded array set instruction`() {

        val opcode = ARRAY_SET
        val reader = FakeWasmBinaryReader()

        val typeIndex = typeIndex()
        val typeIndexDecoder: TypeIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(typeIndex)
        }

        val expected = AggregateInstruction.ArraySet(typeIndex)

        val actual = BinaryPrefixFBInstructionDecoder(
            reader = reader,
            opcode = opcode,
            dataIndexDecoder = neverDataIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            fieldIndexDecoder = neverFieldIndexDecoder,
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = neverHeapTypeDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            castFlagsDecoder = neverCastFlagsDecoder,
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded array len instruction`() {

        val opcode = ARRAY_LEN
        val reader = FakeWasmBinaryReader()

        val typeIndex = typeIndex()
        val typeIndexDecoder: TypeIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(typeIndex)
        }

        val expected = AggregateInstruction.ArrayLen

        val actual = BinaryPrefixFBInstructionDecoder(
            reader = reader,
            opcode = opcode,
            dataIndexDecoder = neverDataIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            fieldIndexDecoder = neverFieldIndexDecoder,
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = neverHeapTypeDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            castFlagsDecoder = neverCastFlagsDecoder,
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded array fill instruction`() {

        val opcode = ARRAY_FILL
        val reader = FakeWasmBinaryReader()

        val typeIndex = typeIndex()
        val typeIndexDecoder: TypeIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(typeIndex)
        }

        val expected = AggregateInstruction.ArrayFill(typeIndex)

        val actual = BinaryPrefixFBInstructionDecoder(
            reader = reader,
            opcode = opcode,
            dataIndexDecoder = neverDataIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            fieldIndexDecoder = neverFieldIndexDecoder,
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = neverHeapTypeDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            castFlagsDecoder = neverCastFlagsDecoder,
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded array copy instruction`() {

        val opcode = ARRAY_COPY
        val reader = FakeWasmBinaryReader()

        val typeIndex = typeIndex()
        val typeIndexDecoder: TypeIndexDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(typeIndex)
        }

        val expected = AggregateInstruction.ArrayCopy(typeIndex, typeIndex)

        val actual = BinaryPrefixFBInstructionDecoder(
            reader = reader,
            opcode = opcode,
            dataIndexDecoder = neverDataIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            fieldIndexDecoder = neverFieldIndexDecoder,
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = neverHeapTypeDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            castFlagsDecoder = neverCastFlagsDecoder,
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded array init data instruction`() {

        val opcode = ARRAY_INIT_DATA
        val reader = FakeWasmBinaryReader()

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

        val actual = BinaryPrefixFBInstructionDecoder(
            reader = reader,
            opcode = opcode,
            dataIndexDecoder = dataIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            fieldIndexDecoder = neverFieldIndexDecoder,
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = neverHeapTypeDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            castFlagsDecoder = neverCastFlagsDecoder,
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded array init element instruction`() {

        val opcode = ARRAY_INIT_ELEMENT
        val reader = FakeWasmBinaryReader()

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

        val actual = BinaryPrefixFBInstructionDecoder(
            reader = reader,
            opcode = opcode,
            dataIndexDecoder = neverDataIndexDecoder,
            elementIndexDecoder = elementIndexDecoder,
            fieldIndexDecoder = neverFieldIndexDecoder,
            typeIndexDecoder = typeIndexDecoder,
            heapTypeDecoder = neverHeapTypeDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            castFlagsDecoder = neverCastFlagsDecoder,
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded ref test instruction`() {

        val opcode = REF_TEST
        val reader = FakeWasmBinaryReader()

        val heapType = heapType()
        val heapTypeDecoder: HeapTypeDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(heapType)
        }

        val expected = ReferenceInstruction.RefTest(
            ReferenceType.Ref(heapType),
        )

        val actual = BinaryPrefixFBInstructionDecoder(
            reader = reader,
            opcode = opcode,
            dataIndexDecoder = neverDataIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            fieldIndexDecoder = neverFieldIndexDecoder,
            typeIndexDecoder = neverTypeIndexDecoder,
            heapTypeDecoder = heapTypeDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            castFlagsDecoder = neverCastFlagsDecoder,
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded ref test instruction with a null ref`() {

        val opcode = REF_TEST_NULL
        val reader = FakeWasmBinaryReader()

        val heapType = heapType()
        val heapTypeDecoder: HeapTypeDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(heapType)
        }

        val expected = ReferenceInstruction.RefTest(
            ReferenceType.RefNull(heapType),
        )

        val actual = BinaryPrefixFBInstructionDecoder(
            reader = reader,
            opcode = opcode,
            dataIndexDecoder = neverDataIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            fieldIndexDecoder = neverFieldIndexDecoder,
            typeIndexDecoder = neverTypeIndexDecoder,
            heapTypeDecoder = heapTypeDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            castFlagsDecoder = neverCastFlagsDecoder,
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded ref cast instruction`() {

        val opcode = REF_CAST
        val reader = FakeWasmBinaryReader()

        val heapType = heapType()
        val heapTypeDecoder: HeapTypeDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(heapType)
        }

        val expected = ReferenceInstruction.RefCast(
            ReferenceType.Ref(heapType),
        )

        val actual = BinaryPrefixFBInstructionDecoder(
            reader = reader,
            opcode = opcode,
            dataIndexDecoder = neverDataIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            fieldIndexDecoder = neverFieldIndexDecoder,
            typeIndexDecoder = neverTypeIndexDecoder,
            heapTypeDecoder = heapTypeDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            castFlagsDecoder = neverCastFlagsDecoder,
        )

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can decode an encoded ref cast instruction with a null ref`() {

        val opcode = REF_CAST_NULL
        val reader = FakeWasmBinaryReader()

        val heapType = heapType()
        val heapTypeDecoder: HeapTypeDecoder = { _reader ->
            assertEquals(reader, _reader)
            Ok(heapType)
        }

        val expected = ReferenceInstruction.RefCast(
            ReferenceType.RefNull(heapType),
        )

        val actual = BinaryPrefixFBInstructionDecoder(
            reader = reader,
            opcode = opcode,
            dataIndexDecoder = neverDataIndexDecoder,
            elementIndexDecoder = neverElementIndexDecoder,
            fieldIndexDecoder = neverFieldIndexDecoder,
            typeIndexDecoder = neverTypeIndexDecoder,
            heapTypeDecoder = heapTypeDecoder,
            labelIndexDecoder = neverLabelIndexDecoder,
            castFlagsDecoder = neverCastFlagsDecoder,
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

        aggregateInstructionMap.forEach { (opcode, instruction) ->
            val reader = FakeWasmBinaryReader()

            val actual = BinaryPrefixFBInstructionDecoder(
                reader = reader,
                opcode = opcode,
                dataIndexDecoder = neverDataIndexDecoder,
                elementIndexDecoder = neverElementIndexDecoder,
                fieldIndexDecoder = neverFieldIndexDecoder,
                typeIndexDecoder = neverTypeIndexDecoder,
                heapTypeDecoder = neverHeapTypeDecoder,
                labelIndexDecoder = neverLabelIndexDecoder,
                castFlagsDecoder = neverCastFlagsDecoder,
            )

            assertEquals(Ok(instruction), actual)
        }
    }

    private companion object {
        private val neverCastFlagsDecoder: CastFlagsDecoder = { _ ->
            fail("data index decoder should not run in this scenario")
        }
        private val neverDataIndexDecoder: DataIndexDecoder = { _ ->
            fail("data index decoder should not run in this scenario")
        }
        private val neverElementIndexDecoder: ElementIndexDecoder = { _ ->
            fail("element index decoder should not run in this scenario")
        }
        private val neverFieldIndexDecoder: FieldIndexDecoder = { _ ->
            fail("field index decoder should not run in this scenario")
        }
        private val neverHeapTypeDecoder: HeapTypeDecoder = { _ ->
            fail("field index decoder should not run in this scenario")
        }
        private val neverLabelIndexDecoder: LabelIndexDecoder = { _ ->
            fail("label index decoder should not run in this scenario")
        }
        private val neverTypeIndexDecoder: TypeIndexDecoder = { _ ->
            fail("table index decoder should not run in this scenario")
        }
    }
}
