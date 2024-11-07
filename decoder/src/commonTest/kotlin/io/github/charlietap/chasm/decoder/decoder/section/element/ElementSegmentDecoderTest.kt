package io.github.charlietap.chasm.decoder.decoder.section.element

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.type.reference.REFERENCE_TYPE_REF_NULL
import io.github.charlietap.chasm.decoder.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.fixture.ioError
import io.github.charlietap.chasm.decoder.reader.FakeUIntReader
import io.github.charlietap.chasm.decoder.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.decoder.reader.IOErrorWasmFileReader
import io.github.charlietap.chasm.fixture.module.elementIndex
import io.github.charlietap.chasm.fixture.module.functionIndex
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ElementSegmentDecoderTest {

    @Test
    fun `can decode an active with no table element segment`() {

        val segmentId = SEGMENT_TYPE_ACTIVE_NO_TABLE

        val reader = FakeUIntReader {
            Ok(segmentId)
        }
        val context = decoderContext(reader)

        val elementIndex = elementIndex()
        val offsetExpression = Expression(emptyList())
        val mode = ElementSegment.Mode.Active(Index.TableIndex(0u), offsetExpression)

        val functionIndex1 = functionIndex(117u)
        val functionIndex2 = functionIndex(118u)

        val expected = Ok(
            ElementSegment(
                elementIndex,
                ReferenceType.Ref(AbstractHeapType.Func),
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

        val expressionDecoder: Decoder<Expression> = { _ ->
            Ok(offsetExpression)
        }

        val actual = ElementSegmentDecoder(
            context = context,
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
        val context = decoderContext(reader)

        val elementIndex = elementIndex()
        val mode = ElementSegment.Mode.Passive

        val functionIndex1 = functionIndex(117u)
        val functionIndex2 = functionIndex(118u)

        val expected = Ok(
            ElementSegment(
                elementIndex,
                ReferenceType.Ref(AbstractHeapType.Func),
                listOf(
                    Expression(ReferenceInstruction.RefFunc(functionIndex1)),
                    Expression(ReferenceInstruction.RefFunc(functionIndex2)),
                ),
                mode,
            ),
        )

        val elementKindDecoder: Decoder<ElementKind> = { _ ->
            Ok(ElementKind.FuncRef)
        }

        val functionIndexVectorDecoder: VectorDecoder<Index.FunctionIndex> = { _, _ ->
            Ok(Vector(listOf(functionIndex1, functionIndex2)))
        }

        val actual = ElementSegmentDecoder(
            context = context,
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
        val context = decoderContext(reader)

        val elementIndex = elementIndex()
        val tableIndex = Index.TableIndex(117u)
        val offsetExpression = Expression(emptyList())
        val mode = ElementSegment.Mode.Active(tableIndex, offsetExpression)

        val functionIndex1 = functionIndex(117u)
        val functionIndex2 = functionIndex(118u)

        val expected = Ok(
            ElementSegment(
                elementIndex,
                ReferenceType.Ref(AbstractHeapType.Func),
                listOf(
                    Expression(ReferenceInstruction.RefFunc(functionIndex1)),
                    Expression(ReferenceInstruction.RefFunc(functionIndex2)),
                ),
                mode,
            ),
        )

        val elementKindDecoder: Decoder<ElementKind> = { _ ->
            Ok(ElementKind.FuncRef)
        }

        val expressionDecoder: Decoder<Expression> = { _ ->
            Ok(offsetExpression)
        }

        val functionIndexVectorDecoder: VectorDecoder<Index.FunctionIndex> = { _, _ ->
            Ok(Vector(listOf(functionIndex1, functionIndex2)))
        }

        val tableIndexDecoder: Decoder<Index.TableIndex> = { _ ->
            Ok(tableIndex)
        }

        val actual = ElementSegmentDecoder(
            context = context,
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
        val context = decoderContext(reader)

        val elementIndex = elementIndex()
        val tableIndex = Index.TableIndex(117u)
        val mode = ElementSegment.Mode.Declarative

        val functionIndex1 = functionIndex(117u)
        val functionIndex2 = functionIndex(118u)

        val expected = Ok(
            ElementSegment(
                elementIndex,
                ReferenceType.Ref(AbstractHeapType.Func),
                listOf(
                    Expression(ReferenceInstruction.RefFunc(functionIndex1)),
                    Expression(ReferenceInstruction.RefFunc(functionIndex2)),
                ),
                mode,
            ),
        )

        val elementKindDecoder: Decoder<ElementKind> = { _ ->
            Ok(ElementKind.FuncRef)
        }

        val functionIndexVectorDecoder: VectorDecoder<Index.FunctionIndex> = { _, _ ->
            Ok(Vector(listOf(functionIndex1, functionIndex2)))
        }

        val tableIndexDecoder: Decoder<Index.TableIndex> = { _ ->
            Ok(tableIndex)
        }

        val actual = ElementSegmentDecoder(
            context = context,
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
        val context = decoderContext(reader)

        val elementIndex = elementIndex()
        val tableIndex = Index.TableIndex(0u)
        val offsetExpression = Expression(emptyList())
        val mode = ElementSegment.Mode.Active(tableIndex, offsetExpression)

        val expected = Ok(
            ElementSegment(
                elementIndex,
                ReferenceType.RefNull(AbstractHeapType.Func),
                emptyList(),
                mode,
            ),
        )

        val expressionDecoder: Decoder<Expression> = { _ ->
            Ok(offsetExpression)
        }

        val vectorExpressionDecoder: VectorDecoder<Expression> = { _, _ ->
            Ok(Vector(emptyList()))
        }

        val actual = ElementSegmentDecoder(
            context = context,
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
        val opcode = REFERENCE_TYPE_REF_NULL

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
        val context = decoderContext(reader)

        val elementIndex = elementIndex()
        val mode = ElementSegment.Mode.Passive

        val expected = Ok(
            ElementSegment(
                elementIndex,
                ReferenceType.RefNull(AbstractHeapType.Func),
                emptyList(),
                mode,
            ),
        )

        val referenceTypeDecoder: Decoder<ReferenceType> = { _context ->
            assertEquals(context, _context)
            Ok(ReferenceType.RefNull(AbstractHeapType.Func))
        }

        val vectorExpressionDecoder: VectorDecoder<Expression> = { _, _ ->
            Ok(Vector(emptyList()))
        }

        val actual = ElementSegmentDecoder(
            context = context,
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
        val opcode = REFERENCE_TYPE_REF_NULL

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
        val context = decoderContext(reader)

        val elementIndex = elementIndex()
        val tableIndex = Index.TableIndex(0u)
        val offsetExpression = Expression(emptyList())
        val mode = ElementSegment.Mode.Active(tableIndex, offsetExpression)

        val expected = Ok(
            ElementSegment(
                elementIndex,
                ReferenceType.RefNull(AbstractHeapType.Func),
                emptyList(),
                mode,
            ),
        )

        val expressionDecoder: Decoder<Expression> = { _ ->
            Ok(offsetExpression)
        }

        val referenceTypeDecoder: Decoder<ReferenceType> = { _context ->
            assertEquals(context, _context)
            Ok(ReferenceType.RefNull(AbstractHeapType.Func))
        }

        val tableIndexDecoder: Decoder<Index.TableIndex> = { _ ->
            Ok(tableIndex)
        }

        val vectorExpressionDecoder: VectorDecoder<Expression> = { _, _ ->
            Ok(Vector(emptyList()))
        }

        val actual = ElementSegmentDecoder(
            context = context,
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
        val opcode = REFERENCE_TYPE_REF_NULL

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
        val context = decoderContext(reader)

        val elementIndex = elementIndex()
        val mode = ElementSegment.Mode.Declarative

        val expected = Ok(
            ElementSegment(
                elementIndex,
                ReferenceType.RefNull(AbstractHeapType.Func),
                emptyList(),
                mode,
            ),
        )

        val referenceTypeDecoder: Decoder<ReferenceType> = { _context ->
            assertEquals(context, _context)
            Ok(ReferenceType.RefNull(AbstractHeapType.Func))
        }

        val vectorExpressionDecoder: VectorDecoder<Expression> = { _, _ ->
            Ok(Vector(emptyList()))
        }

        val actual = ElementSegmentDecoder(
            context = context,
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
        val context = decoderContext(reader)

        val actual = ElementSegmentDecoder(context)

        assertEquals(expected, actual)
    }

    internal companion object {
        val neverFunctionIndexDecoder: Decoder<Index.FunctionIndex> = { _ ->
            fail("function indexx decoder should not be called in this scenario")
        }

        val neverFunctionIndexVectorDecoder: VectorDecoder<Index.FunctionIndex> = { _, _ ->
            fail("function index decoder should not be called in this scenario")
        }

        val neverElementKindDecoder: Decoder<ElementKind> = { _ ->
            fail("element kind decoder should not be called in this scenario")
        }

        val neverExpressionDecoder: Decoder<Expression> = { _ ->
            fail("expression decoder should not be called in this scenario")
        }

        val neverExpressionVectorDecoder: VectorDecoder<Expression> = { _, _ ->
            fail("expression vector decoder should not be called in this scenario")
        }

        val neverTableIndexDecoder: Decoder<Index.TableIndex> = { _ ->
            fail("table index decoder should not be called in this scenario")
        }

        val neverReferenceTypeDecoder: Decoder<ReferenceType> = { _ ->
            fail("reference type decoder should not be called in this scenario")
        }
    }
}
