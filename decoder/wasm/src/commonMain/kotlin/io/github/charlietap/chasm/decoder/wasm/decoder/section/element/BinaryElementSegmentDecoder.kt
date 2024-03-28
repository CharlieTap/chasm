package io.github.charlietap.chasm.decoder.wasm.decoder.section.element

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.BinaryExpressionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.ExpressionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.BinaryFunctionIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.BinaryTableIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.FunctionIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.TableIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.reference.BinaryReferenceTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.reference.ReferenceTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.BinaryVectorDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.error.SectionDecodeError
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryElementSegmentDecoder(
    reader: WasmBinaryReader,
    elemIdx: Index.ElementIndex,
): Result<ElementSegment, WasmDecodeError> =
    BinaryElementSegmentDecoder(
        reader = reader,
        elemIdx = elemIdx,
        elementKindDecoder = ::BinaryElementKindDecoder,
        expressionDecoder = ::BinaryExpressionDecoder,
        functionIndexDecoder = ::BinaryFunctionIndexDecoder,
        tableIndexDecoder = ::BinaryTableIndexDecoder,
        referenceTypeDecoder = ::BinaryReferenceTypeDecoder,
        expressionVectorDecoder = ::BinaryVectorDecoder,
        functionIndexVectorDecoder = ::BinaryVectorDecoder,
    )

internal fun BinaryElementSegmentDecoder(
    reader: WasmBinaryReader,
    elemIdx: Index.ElementIndex,
    elementKindDecoder: ElementKindDecoder,
    expressionDecoder: ExpressionDecoder,
    functionIndexDecoder: FunctionIndexDecoder,
    tableIndexDecoder: TableIndexDecoder,
    referenceTypeDecoder: ReferenceTypeDecoder,
    expressionVectorDecoder: VectorDecoder<Expression>,
    functionIndexVectorDecoder: VectorDecoder<Index.FunctionIndex>,
): Result<ElementSegment, WasmDecodeError> = binding {

    when (val segmentId = reader.uint().bind()) {
        SEGMENT_TYPE_ACTIVE_NO_TABLE -> {

            val offSetExpression = expressionDecoder(reader).bind()
            val indices = functionIndexVectorDecoder(reader, functionIndexDecoder).bind()
            val mode = ElementSegment.Mode.Active(Index.TableIndex(0u), offSetExpression)

            val expressions: List<Expression> = indices.vector.map { idx ->
                Expression(listOf(ReferenceInstruction.RefFunc(idx)))
            }

            ElementSegment(elemIdx, ReferenceType.RefNull(HeapType.Func), expressions, mode)
        }
        SEGMENT_TYPE_PASSIVE_FUNCREFS -> {

            val elemKind = elementKindDecoder(reader).bind()
            val refType = elemKind.let { kind ->
                when (kind) {
                    ElementKind.FuncRef -> ReferenceType.RefNull(HeapType.Func)
                }
            }

            val indices = functionIndexVectorDecoder(reader, functionIndexDecoder).bind()
            val expressions: List<Expression> = indices.vector.map { idx ->
                Expression(listOf(ReferenceInstruction.RefFunc(idx)))
            }

            val mode = ElementSegment.Mode.Passive

            ElementSegment(elemIdx, refType, expressions, mode)
        }
        SEGMENT_TYPE_ACTIVE_TABLEIDX_FUNCREFS_WITH_OFFSET -> {

            val tableIndex = tableIndexDecoder(reader).bind()
            val offsetExpression = expressionDecoder(reader).bind()

            val mode = ElementSegment.Mode.Active(tableIndex, offsetExpression)

            val elemKind = elementKindDecoder(reader).bind()
            val refType = elemKind.let { kind ->
                when (kind) {
                    ElementKind.FuncRef -> ReferenceType.RefNull(HeapType.Func)
                }
            }

            val indices = functionIndexVectorDecoder(reader, functionIndexDecoder).bind()
            val expressions: List<Expression> = indices.vector.map { idx ->
                Expression(listOf(ReferenceInstruction.RefFunc(idx)))
            }

            ElementSegment(elemIdx, refType, expressions, mode)
        }
        SEGMENT_TYPE_DECLARATIVE_FUNCREFS -> {

            val elemKind = elementKindDecoder(reader).bind()
            val refType = elemKind.let { kind ->
                when (kind) {
                    ElementKind.FuncRef -> ReferenceType.RefNull(HeapType.Func)
                }
            }

            val indices = functionIndexVectorDecoder(reader, functionIndexDecoder).bind()
            val expressions: List<Expression> = indices.vector.map { idx ->
                Expression(listOf(ReferenceInstruction.RefFunc(idx)))
            }

            val mode = ElementSegment.Mode.Declarative

            ElementSegment(elemIdx, refType, expressions, mode)
        }
        SEGMENT_TYPE_ACTIVE_NO_TABLE_MANY_EXPRESSIONS -> {

            val offSetExpression = expressionDecoder(reader).bind()
            val mode = ElementSegment.Mode.Active(Index.TableIndex(0u), offSetExpression)

            val expressions = expressionVectorDecoder(reader, expressionDecoder).bind()

            ElementSegment(elemIdx, ReferenceType.RefNull(HeapType.Func), expressions.vector, mode)
        }
        SEGMENT_TYPE_PASSIVE_MANY_EXPRESSIONS -> {

            val referenceType = referenceTypeDecoder(reader, reader.ubyte().bind()).bind()
            val expressions = expressionVectorDecoder(reader, expressionDecoder).bind()

            ElementSegment(elemIdx, referenceType, expressions.vector, ElementSegment.Mode.Passive)
        }
        SEGMENT_TYPE_ACTIVE_W_TABLEIDX -> {

            val tableIndex = tableIndexDecoder(reader).bind()
            val offSetExpression = expressionDecoder(reader).bind()
            val mode = ElementSegment.Mode.Active(tableIndex, offSetExpression)

            val referenceType = referenceTypeDecoder(reader, reader.ubyte().bind()).bind()
            val expressions = expressionVectorDecoder(reader, expressionDecoder).bind()

            ElementSegment(elemIdx, referenceType, expressions.vector, mode)
        }
        SEGMENT_TYPE_DECLARATIVE_MANY_EXPRESSIONS -> {

            val referenceType = referenceTypeDecoder(reader, reader.ubyte().bind()).bind()
            val expressions = expressionVectorDecoder(reader, expressionDecoder).bind()

            ElementSegment(elemIdx, referenceType, expressions.vector, ElementSegment.Mode.Declarative)
        }
        else -> Err(SectionDecodeError.UnknownElementSegment(segmentId)).bind<ElementSegment>()
    }
}

internal const val SEGMENT_TYPE_ACTIVE_NO_TABLE = 0u
internal const val SEGMENT_TYPE_PASSIVE_FUNCREFS = 1u
internal const val SEGMENT_TYPE_ACTIVE_TABLEIDX_FUNCREFS_WITH_OFFSET = 2u
internal const val SEGMENT_TYPE_DECLARATIVE_FUNCREFS = 3u
internal const val SEGMENT_TYPE_ACTIVE_NO_TABLE_MANY_EXPRESSIONS = 4u
internal const val SEGMENT_TYPE_PASSIVE_MANY_EXPRESSIONS = 5u
internal const val SEGMENT_TYPE_ACTIVE_W_TABLEIDX = 6u
internal const val SEGMENT_TYPE_DECLARATIVE_MANY_EXPRESSIONS = 7u
