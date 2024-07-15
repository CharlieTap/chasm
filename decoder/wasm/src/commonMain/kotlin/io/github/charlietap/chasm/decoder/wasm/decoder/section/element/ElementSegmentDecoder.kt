package io.github.charlietap.chasm.decoder.wasm.decoder.section.element

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.decoder.wasm.Decoder
import io.github.charlietap.chasm.decoder.wasm.context.DecoderContext
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.ExpressionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.FunctionIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.TableIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.reference.ReferenceTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.error.SectionDecodeError
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError

internal fun ElementSegmentDecoder(
    context: DecoderContext,
): Result<ElementSegment, WasmDecodeError> =
    ElementSegmentDecoder(
        context = context,
        elementKindDecoder = ::ElementKindDecoder,
        expressionDecoder = ::ExpressionDecoder,
        functionIndexDecoder = ::FunctionIndexDecoder,
        tableIndexDecoder = ::TableIndexDecoder,
        referenceTypeDecoder = ::ReferenceTypeDecoder,
        expressionVectorDecoder = ::VectorDecoder,
        functionIndexVectorDecoder = ::VectorDecoder,
    )

internal fun ElementSegmentDecoder(
    context: DecoderContext,
    elementKindDecoder: Decoder<ElementKind>,
    expressionDecoder: Decoder<Expression>,
    functionIndexDecoder: Decoder<Index.FunctionIndex>,
    tableIndexDecoder: Decoder<Index.TableIndex>,
    referenceTypeDecoder: Decoder<ReferenceType>,
    expressionVectorDecoder: VectorDecoder<Expression>,
    functionIndexVectorDecoder: VectorDecoder<Index.FunctionIndex>,
): Result<ElementSegment, WasmDecodeError> = binding {

    val elemIndexPlaceholder = Index.ElementIndex(0u)

    when (val segmentId = context.reader.uint().bind()) {
        SEGMENT_TYPE_ACTIVE_NO_TABLE -> {

            val offSetExpression = expressionDecoder(context).bind()
            val indices = functionIndexVectorDecoder(context, functionIndexDecoder).bind()
            val mode = ElementSegment.Mode.Active(Index.TableIndex(0u), offSetExpression)

            val expressions: List<Expression> = indices.vector.map { idx ->
                Expression(listOf(ReferenceInstruction.RefFunc(idx)))
            }

            ElementSegment(elemIndexPlaceholder, ReferenceType.RefNull(AbstractHeapType.Func), expressions, mode)
        }
        SEGMENT_TYPE_PASSIVE_FUNCREFS -> {

            val elemKind = elementKindDecoder(context).bind()
            val refType = elemKind.let { kind ->
                when (kind) {
                    ElementKind.FuncRef -> ReferenceType.RefNull(AbstractHeapType.Func)
                }
            }

            val indices = functionIndexVectorDecoder(context, functionIndexDecoder).bind()
            val expressions: List<Expression> = indices.vector.map { idx ->
                Expression(listOf(ReferenceInstruction.RefFunc(idx)))
            }

            val mode = ElementSegment.Mode.Passive

            ElementSegment(elemIndexPlaceholder, refType, expressions, mode)
        }
        SEGMENT_TYPE_ACTIVE_TABLEIDX_FUNCREFS_WITH_OFFSET -> {

            val tableIndex = tableIndexDecoder(context).bind()
            val offsetExpression = expressionDecoder(context).bind()

            val mode = ElementSegment.Mode.Active(tableIndex, offsetExpression)

            val elemKind = elementKindDecoder(context).bind()
            val refType = elemKind.let { kind ->
                when (kind) {
                    ElementKind.FuncRef -> ReferenceType.RefNull(AbstractHeapType.Func)
                }
            }

            val indices = functionIndexVectorDecoder(context, functionIndexDecoder).bind()
            val expressions: List<Expression> = indices.vector.map { idx ->
                Expression(listOf(ReferenceInstruction.RefFunc(idx)))
            }

            ElementSegment(elemIndexPlaceholder, refType, expressions, mode)
        }
        SEGMENT_TYPE_DECLARATIVE_FUNCREFS -> {

            val elemKind = elementKindDecoder(context).bind()
            val refType = elemKind.let { kind ->
                when (kind) {
                    ElementKind.FuncRef -> ReferenceType.RefNull(AbstractHeapType.Func)
                }
            }

            val indices = functionIndexVectorDecoder(context, functionIndexDecoder).bind()
            val expressions: List<Expression> = indices.vector.map { idx ->
                Expression(listOf(ReferenceInstruction.RefFunc(idx)))
            }

            val mode = ElementSegment.Mode.Declarative

            ElementSegment(elemIndexPlaceholder, refType, expressions, mode)
        }
        SEGMENT_TYPE_ACTIVE_NO_TABLE_MANY_EXPRESSIONS -> {

            val offSetExpression = expressionDecoder(context).bind()
            val mode = ElementSegment.Mode.Active(Index.TableIndex(0u), offSetExpression)

            val expressions = expressionVectorDecoder(context, expressionDecoder).bind()

            ElementSegment(elemIndexPlaceholder, ReferenceType.RefNull(AbstractHeapType.Func), expressions.vector, mode)
        }
        SEGMENT_TYPE_PASSIVE_MANY_EXPRESSIONS -> {

            val referenceType = referenceTypeDecoder(context).bind()
            val expressions = expressionVectorDecoder(context, expressionDecoder).bind()

            ElementSegment(elemIndexPlaceholder, referenceType, expressions.vector, ElementSegment.Mode.Passive)
        }
        SEGMENT_TYPE_ACTIVE_W_TABLEIDX -> {

            val tableIndex = tableIndexDecoder(context).bind()
            val offSetExpression = expressionDecoder(context).bind()
            val mode = ElementSegment.Mode.Active(tableIndex, offSetExpression)

            val referenceType = referenceTypeDecoder(context).bind()
            val expressions = expressionVectorDecoder(context, expressionDecoder).bind()

            ElementSegment(elemIndexPlaceholder, referenceType, expressions.vector, mode)
        }
        SEGMENT_TYPE_DECLARATIVE_MANY_EXPRESSIONS -> {

            val referenceType = referenceTypeDecoder(context).bind()
            val expressions = expressionVectorDecoder(context, expressionDecoder).bind()

            ElementSegment(elemIndexPlaceholder, referenceType, expressions.vector, ElementSegment.Mode.Declarative)
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
