package io.github.charlietap.chasm.decoder.wasm.decoder.section.element

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.ExpressionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.FunctionIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.TableIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.reference.ReferenceTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.value.VALUE_TYPE_REFERENCE_REF_NULL
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.fixture.ioError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUIntReader
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.reader.IOErrorWasmFileReader
import io.github.charlietap.chasm.fixture.instruction.functionIndex
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class BinaryElementSegmentDecoderTest {

    @Test
    fun `can decode an active with no table element segment`() {

        val segmentId = SEGMENT_TYPE_ACTIVE_NO_TABLE

        val reader = FakeUIntReader {
            Ok(segmentId)
        }

        val elementIndex = Index.ElementIndex(117u)
        val offsetExpression = Expression(emptyList())
        val mode = ElementSegment.Mode.Active(Index.TableIndex(0u), offsetExpression)

        val functionIndex1 = functionIndex(117u)
        val functionIndex2 = functionIndex(118u)

        val expected = Ok(
            ElementSegment(
                elementIndex,
                ReferenceType.RefNull(HeapType.Func),
                listOf(
                    Expression(ReferenceInstruction.RefFunc(functionIndex1)),
                    Expression(ReferenceInstruction.RefFunc(functionIndex2)),
                ),
                mode,
            ),
        )

        val functionIndexVectorDecoder: VectorDecoder<Index.FunctionIndex> = { _, _ ->
            Ok(Vector(listOf(functionIndex1, functionIndex2)))
        }

        val expressionDecoder: ExpressionDecoder = { _ ->
            Ok(offsetExpression)
        }

        val actual = BinaryElementSegmentDecoder(
            reader = reader,
            elemIdx = elementIndex,
            elementKindDecoder = neverElementKindDecoder,
            expressionDecoder = expressionDecoder,
            functionIndexDecoder = neverFunctionIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
            referenceTypeDecoder = neverReferenceTypeDecoder,
            expressionVectorDecoder = neverExpressionVectorDecoder,
            functionIndexVectorDecoder = functionIndexVectorDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a passive func refs element segment`() {

        val segmentId = SEGMENT_TYPE_PASSIVE_FUNCREFS

        val reader = FakeUIntReader {
            Ok(segmentId)
        }

        val elementIndex = Index.ElementIndex(117u)
        val mode = ElementSegment.Mode.Passive

        val functionIndex1 = functionIndex(117u)
        val functionIndex2 = functionIndex(118u)

        val expected = Ok(
            ElementSegment(
                elementIndex,
                ReferenceType.RefNull(HeapType.Func),
                listOf(
                    Expression(ReferenceInstruction.RefFunc(functionIndex1)),
                    Expression(ReferenceInstruction.RefFunc(functionIndex2)),
                ),
                mode,
            ),
        )

        val elementKindDecoder: ElementKindDecoder = { _ ->
            Ok(ElementKind.FuncRef)
        }

        val functionIndexVectorDecoder: VectorDecoder<Index.FunctionIndex> = { _, _ ->
            Ok(Vector(listOf(functionIndex1, functionIndex2)))
        }

        val actual = BinaryElementSegmentDecoder(
            reader = reader,
            elemIdx = elementIndex,
            elementKindDecoder = elementKindDecoder,
            expressionDecoder = neverExpressionDecoder,
            functionIndexDecoder = neverFunctionIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
            referenceTypeDecoder = neverReferenceTypeDecoder,
            expressionVectorDecoder = neverExpressionVectorDecoder,
            functionIndexVectorDecoder = functionIndexVectorDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an active table idx with offset element segment`() { // naming/10

        val segmentId = SEGMENT_TYPE_ACTIVE_TABLEIDX_FUNCREFS_WITH_OFFSET

        val reader = FakeUIntReader {
            Ok(segmentId)
        }

        val elementIndex = Index.ElementIndex(117u)
        val tableIndex = Index.TableIndex(117u)
        val offsetExpression = Expression(emptyList())
        val mode = ElementSegment.Mode.Active(tableIndex, offsetExpression)

        val functionIndex1 = functionIndex(117u)
        val functionIndex2 = functionIndex(118u)

        val expected = Ok(
            ElementSegment(
                elementIndex,
                ReferenceType.RefNull(HeapType.Func),
                listOf(
                    Expression(ReferenceInstruction.RefFunc(functionIndex1)),
                    Expression(ReferenceInstruction.RefFunc(functionIndex2)),
                ),
                mode,
            ),
        )

        val elementKindDecoder: ElementKindDecoder = { _ ->
            Ok(ElementKind.FuncRef)
        }

        val expressionDecoder: ExpressionDecoder = { _ ->
            Ok(offsetExpression)
        }

        val functionIndexVectorDecoder: VectorDecoder<Index.FunctionIndex> = { _, _ ->
            Ok(Vector(listOf(functionIndex1, functionIndex2)))
        }

        val tableIndexDecoder: TableIndexDecoder = { _ ->
            Ok(tableIndex)
        }

        val actual = BinaryElementSegmentDecoder(
            reader = reader,
            elemIdx = elementIndex,
            elementKindDecoder = elementKindDecoder,
            expressionDecoder = expressionDecoder,
            functionIndexDecoder = neverFunctionIndexDecoder,
            tableIndexDecoder = tableIndexDecoder,
            referenceTypeDecoder = neverReferenceTypeDecoder,
            expressionVectorDecoder = neverExpressionVectorDecoder,
            functionIndexVectorDecoder = functionIndexVectorDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a declarative element segment`() {

        val segmentId = SEGMENT_TYPE_DECLARATIVE_FUNCREFS

        val reader = FakeUIntReader {
            Ok(segmentId)
        }

        val elementIndex = Index.ElementIndex(117u)
        val tableIndex = Index.TableIndex(117u)
        val mode = ElementSegment.Mode.Declarative

        val functionIndex1 = functionIndex(117u)
        val functionIndex2 = functionIndex(118u)

        val expected = Ok(
            ElementSegment(
                elementIndex,
                ReferenceType.RefNull(HeapType.Func),
                listOf(
                    Expression(ReferenceInstruction.RefFunc(functionIndex1)),
                    Expression(ReferenceInstruction.RefFunc(functionIndex2)),
                ),
                mode,
            ),
        )

        val elementKindDecoder: ElementKindDecoder = { _ ->
            Ok(ElementKind.FuncRef)
        }

        val functionIndexVectorDecoder: VectorDecoder<Index.FunctionIndex> = { _, _ ->
            Ok(Vector(listOf(functionIndex1, functionIndex2)))
        }

        val tableIndexDecoder: TableIndexDecoder = { _ ->
            Ok(tableIndex)
        }

        val actual = BinaryElementSegmentDecoder(
            reader = reader,
            elemIdx = elementIndex,
            elementKindDecoder = elementKindDecoder,
            expressionDecoder = neverExpressionDecoder,
            functionIndexDecoder = neverFunctionIndexDecoder,
            tableIndexDecoder = tableIndexDecoder,
            referenceTypeDecoder = neverReferenceTypeDecoder,
            expressionVectorDecoder = neverExpressionVectorDecoder,
            functionIndexVectorDecoder = functionIndexVectorDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an active table many expressions  element segment`() {

        val segmentId = SEGMENT_TYPE_ACTIVE_NO_TABLE_MANY_EXPRESSIONS

        val reader = FakeUIntReader {
            Ok(segmentId)
        }

        val elementIndex = Index.ElementIndex(117u)
        val tableIndex = Index.TableIndex(0u)
        val offsetExpression = Expression(emptyList())
        val mode = ElementSegment.Mode.Active(tableIndex, offsetExpression)

        val expected = Ok(
            ElementSegment(
                elementIndex,
                ReferenceType.RefNull(HeapType.Func),
                emptyList(),
                mode,
            ),
        )

        val expressionDecoder: ExpressionDecoder = { _ ->
            Ok(offsetExpression)
        }

        val vectorExpressionDecoder: VectorDecoder<Expression> = { _, _ ->
            Ok(Vector(emptyList()))
        }

        val actual = BinaryElementSegmentDecoder(
            reader = reader,
            elemIdx = elementIndex,
            elementKindDecoder = neverElementKindDecoder,
            expressionDecoder = expressionDecoder,
            functionIndexDecoder = neverFunctionIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
            referenceTypeDecoder = neverReferenceTypeDecoder,
            expressionVectorDecoder = vectorExpressionDecoder,
            functionIndexVectorDecoder = neverFunctionIndexVectorDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a passive many expressions element segment`() {

        val segmentId = SEGMENT_TYPE_PASSIVE_MANY_EXPRESSIONS
        val opcode = VALUE_TYPE_REFERENCE_REF_NULL

        val segmentReader: () -> Result<UInt, WasmDecodeError> = {
            Ok(segmentId)
        }

        val byteReader: () -> Result<UByte, WasmDecodeError> = {
            Ok(opcode)
        }

        val reader = FakeWasmBinaryReader(
            fakeUByteReader = byteReader,
            fakeUIntReader = segmentReader,
        )

        val elementIndex = Index.ElementIndex(117u)
        val mode = ElementSegment.Mode.Passive

        val expected = Ok(
            ElementSegment(
                elementIndex,
                ReferenceType.RefNull(HeapType.Func),
                emptyList(),
                mode,
            ),
        )

        val referenceTypeDecoder: ReferenceTypeDecoder = { _reader, _opcode ->
            assertEquals(reader, _reader)
            assertEquals(opcode, _opcode)
            Ok(ReferenceType.RefNull(HeapType.Func))
        }

        val vectorExpressionDecoder: VectorDecoder<Expression> = { _, _ ->
            Ok(Vector(emptyList()))
        }

        val actual = BinaryElementSegmentDecoder(
            reader = reader,
            elemIdx = elementIndex,
            elementKindDecoder = neverElementKindDecoder,
            expressionDecoder = neverExpressionDecoder,
            functionIndexDecoder = neverFunctionIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
            referenceTypeDecoder = referenceTypeDecoder,
            expressionVectorDecoder = vectorExpressionDecoder,
            functionIndexVectorDecoder = neverFunctionIndexVectorDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an active with table index element segment`() {

        val segmentId = SEGMENT_TYPE_ACTIVE_W_TABLEIDX
        val opcode = VALUE_TYPE_REFERENCE_REF_NULL

        val segmentReader: () -> Result<UInt, WasmDecodeError> = {
            Ok(segmentId)
        }

        val byteReader: () -> Result<UByte, WasmDecodeError> = {
            Ok(opcode)
        }

        val reader = FakeWasmBinaryReader(
            fakeUByteReader = byteReader,
            fakeUIntReader = segmentReader,
        )

        val elementIndex = Index.ElementIndex(117u)
        val tableIndex = Index.TableIndex(0u)
        val offsetExpression = Expression(emptyList())
        val mode = ElementSegment.Mode.Active(tableIndex, offsetExpression)

        val expected = Ok(
            ElementSegment(
                elementIndex,
                ReferenceType.RefNull(HeapType.Func),
                emptyList(),
                mode,
            ),
        )

        val expressionDecoder: ExpressionDecoder = { _ ->
            Ok(offsetExpression)
        }

        val referenceTypeDecoder: ReferenceTypeDecoder = { _reader, _opcode ->
            assertEquals(reader, _reader)
            assertEquals(opcode, _opcode)
            Ok(ReferenceType.RefNull(HeapType.Func))
        }

        val tableIndexDecoder: TableIndexDecoder = { _ ->
            Ok(tableIndex)
        }

        val vectorExpressionDecoder: VectorDecoder<Expression> = { _, _ ->
            Ok(Vector(emptyList()))
        }

        val actual = BinaryElementSegmentDecoder(
            reader = reader,
            elemIdx = elementIndex,
            elementKindDecoder = neverElementKindDecoder,
            expressionDecoder = expressionDecoder,
            functionIndexDecoder = neverFunctionIndexDecoder,
            tableIndexDecoder = tableIndexDecoder,
            referenceTypeDecoder = referenceTypeDecoder,
            expressionVectorDecoder = vectorExpressionDecoder,
            functionIndexVectorDecoder = neverFunctionIndexVectorDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a declarative many expressions element segment`() {

        val segmentId = SEGMENT_TYPE_DECLARATIVE_MANY_EXPRESSIONS
        val opcode = VALUE_TYPE_REFERENCE_REF_NULL

        val segmentReader: () -> Result<UInt, WasmDecodeError> = {
            Ok(segmentId)
        }

        val byteReader: () -> Result<UByte, WasmDecodeError> = {
            Ok(opcode)
        }

        val reader = FakeWasmBinaryReader(
            fakeUByteReader = byteReader,
            fakeUIntReader = segmentReader,
        )

        val elementIndex = Index.ElementIndex(117u)
        val mode = ElementSegment.Mode.Declarative

        val expected = Ok(
            ElementSegment(
                elementIndex,
                ReferenceType.RefNull(HeapType.Func),
                emptyList(),
                mode,
            ),
        )

        val referenceTypeDecoder: ReferenceTypeDecoder = { _reader, _opcode ->
            assertEquals(reader, _reader)
            assertEquals(opcode, _opcode)
            Ok(ReferenceType.RefNull(HeapType.Func))
        }

        val vectorExpressionDecoder: VectorDecoder<Expression> = { _, _ ->
            Ok(Vector(emptyList()))
        }

        val actual = BinaryElementSegmentDecoder(
            reader = reader,
            elemIdx = elementIndex,
            elementKindDecoder = neverElementKindDecoder,
            expressionDecoder = neverExpressionDecoder,
            functionIndexDecoder = neverFunctionIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
            referenceTypeDecoder = referenceTypeDecoder,
            expressionVectorDecoder = vectorExpressionDecoder,
            functionIndexVectorDecoder = neverFunctionIndexVectorDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can catch io exceptions from the wasm file reader`() {
        val expected = ioError()
        val reader = IOErrorWasmFileReader(expected)

        val actual = BinaryElementSegmentDecoder(reader, Index.ElementIndex(117u))

        assertEquals(expected, actual)
    }

    internal companion object {
        val neverFunctionIndexDecoder: FunctionIndexDecoder = { _ ->
            fail("function indexx decoder should not be called in this scenario")
        }

        val neverFunctionIndexVectorDecoder: VectorDecoder<Index.FunctionIndex> = { _, _ ->
            fail("function indexx decoder should not be called in this scenario")
        }

        val neverElementKindDecoder: ElementKindDecoder = { _ ->
            fail("element kind decoder should not be called in this scenario")
        }

        val neverExpressionDecoder: ExpressionDecoder = { _ ->
            fail("expression decoder should not be called in this scenario")
        }

        val neverExpressionVectorDecoder: VectorDecoder<Expression> = { _, _ ->
            fail("expression vector decoder should not be called in this scenario")
        }

        val neverTableIndexDecoder: TableIndexDecoder = { _ ->
            fail("table index decoder should not be called in this scenario")
        }

        val neverReferenceTypeDecoder: ReferenceTypeDecoder = { _, _ ->
            fail("table index decoder should not be called in this scenario")
        }
    }
}
