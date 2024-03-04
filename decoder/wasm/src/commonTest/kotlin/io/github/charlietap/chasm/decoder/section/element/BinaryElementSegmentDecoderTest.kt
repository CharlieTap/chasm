package io.github.charlietap.chasm.decoder.section.element

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.decoder.instruction.ExpressionDecoder
import io.github.charlietap.chasm.decoder.section.index.FunctionIndexDecoder
import io.github.charlietap.chasm.decoder.section.index.TableIndexDecoder
import io.github.charlietap.chasm.decoder.type.reference.ReferenceTypeDecoder
import io.github.charlietap.chasm.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.fixture.ioError
import io.github.charlietap.chasm.reader.FakeUIntReader
import io.github.charlietap.chasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.reader.IOErrorWasmFileReader
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

        val functionIndex = Index.FunctionIndex(117u)

        val expected = Ok(
            ElementSegment(
                elementIndex,
                ReferenceType.Funcref,
                listOf(Expression(ReferenceInstruction.RefFunc(functionIndex))),
                mode,
            ),
        )

        val functionIndexVectorDecoder: VectorDecoder<Index.FunctionIndex> = { _, _ ->
            Ok(Vector(listOf(functionIndex)))
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

        val functionIndex = Index.FunctionIndex(117u)

        val expected = Ok(
            ElementSegment(
                elementIndex,
                ReferenceType.Funcref,
                listOf(Expression(ReferenceInstruction.RefFunc(functionIndex))),
                mode,
            ),
        )

        val elementKindDecoder: ElementKindDecoder = { _ ->
            Ok(ElementKind.FuncRef)
        }

        val functionIndexVectorDecoder: VectorDecoder<Index.FunctionIndex> = { _, _ ->
            Ok(Vector(listOf(functionIndex)))
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

        val functionIndex = Index.FunctionIndex(117u)

        val expected = Ok(
            ElementSegment(
                elementIndex,
                ReferenceType.Funcref,
                listOf(Expression(ReferenceInstruction.RefFunc(functionIndex))),
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
            Ok(Vector(listOf(functionIndex)))
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

        val functionIndex = Index.FunctionIndex(117u)

        val expected = Ok(
            ElementSegment(
                elementIndex,
                ReferenceType.Funcref,
                listOf(Expression(ReferenceInstruction.RefFunc(functionIndex))),
                mode,
            ),
        )

        val elementKindDecoder: ElementKindDecoder = { _ ->
            Ok(ElementKind.FuncRef)
        }

        val functionIndexVectorDecoder: VectorDecoder<Index.FunctionIndex> = { _, _ ->
            Ok(Vector(listOf(functionIndex)))
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
                ReferenceType.Funcref,
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

        val segmentReader: () -> Result<UInt, WasmDecodeError> = {
            Ok(segmentId)
        }

        val byteReader: () -> Result<UByte, WasmDecodeError> = {
            Ok(UByte.MAX_VALUE)
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
                ReferenceType.Funcref,
                emptyList(),
                mode,
            ),
        )

        val referenceTypeDecoder: ReferenceTypeDecoder = { _ ->
            Ok(ReferenceType.Funcref)
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

        val segmentReader: () -> Result<UInt, WasmDecodeError> = {
            Ok(segmentId)
        }

        val byteReader: () -> Result<UByte, WasmDecodeError> = {
            Ok(UByte.MAX_VALUE)
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
                ReferenceType.Funcref,
                emptyList(),
                mode,
            ),
        )

        val expressionDecoder: ExpressionDecoder = { _ ->
            Ok(offsetExpression)
        }

        val referenceTypeDecoder: ReferenceTypeDecoder = { _ ->
            Ok(ReferenceType.Funcref)
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

        val segmentReader: () -> Result<UInt, WasmDecodeError> = {
            Ok(segmentId)
        }

        val byteReader: () -> Result<UByte, WasmDecodeError> = {
            Ok(UByte.MAX_VALUE)
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
                ReferenceType.Funcref,
                emptyList(),
                mode,
            ),
        )

        val referenceTypeDecoder: ReferenceTypeDecoder = { _ ->
            Ok(ReferenceType.Funcref)
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

    companion object {
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

        val neverReferenceTypeDecoder: ReferenceTypeDecoder = { _ ->
            fail("table index decoder should not be called in this scenario")
        }
    }
}
