package io.github.charlietap.chasm.decoder.section.element

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.decoder.instruction.BinaryExpressionDecoder
import io.github.charlietap.chasm.decoder.instruction.ExpressionDecoder
import io.github.charlietap.chasm.decoder.section.index.BinaryFunctionIndexDecoder
import io.github.charlietap.chasm.decoder.section.index.BinaryTableIndexDecoder
import io.github.charlietap.chasm.decoder.section.index.FunctionIndexDecoder
import io.github.charlietap.chasm.decoder.section.index.TableIndexDecoder
import io.github.charlietap.chasm.decoder.type.reference.BinaryReferenceTypeDecoder
import io.github.charlietap.chasm.decoder.type.reference.ReferenceTypeDecoder
import io.github.charlietap.chasm.decoder.vector.BinaryVectorDecoder
import io.github.charlietap.chasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.error.SectionDecodeError
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

fun BinaryElementSegmentDecoder(
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

            val instructions: List<Instruction> = indices.vector.map { idx ->
                ReferenceInstruction.RefFunc(idx)
            }
            val expression = Expression(instructions)

            ElementSegment(elemIdx, ReferenceType.Funcref, listOf(expression), mode)
        }
        SEGMENT_TYPE_PASSIVE_FUNCREFS -> {

            val elemKind = elementKindDecoder(reader).bind()
            val refType = elemKind.let { kind ->
                when (kind) {
                    ElementKind.FuncRef -> ReferenceType.Funcref
                }
            }

            val indices = functionIndexVectorDecoder(reader, functionIndexDecoder).bind()
            val instructions: List<Instruction> = indices.vector.map { idx ->
                ReferenceInstruction.RefFunc(idx)
            }
            val expression = Expression(instructions)

            val mode = ElementSegment.Mode.Passive

            ElementSegment(elemIdx, refType, listOf(expression), mode)
        }
        SEGMENT_TYPE_ACTIVE_TABLEIDX_FUNCREFS_WITH_OFFSET -> {

            val tableIndex = tableIndexDecoder(reader).bind()
            val offsetExpression = expressionDecoder(reader).bind()

            val mode = ElementSegment.Mode.Active(tableIndex, offsetExpression)

            val elemKind = elementKindDecoder(reader).bind()
            val refType = elemKind.let { kind ->
                when (kind) {
                    ElementKind.FuncRef -> ReferenceType.Funcref
                }
            }

            val indices = functionIndexVectorDecoder(reader, functionIndexDecoder).bind()
            val instructions: List<Instruction> = indices.vector.map { idx ->
                ReferenceInstruction.RefFunc(idx)
            }
            val expression = Expression(instructions)

            ElementSegment(elemIdx, refType, listOf(expression), mode)
        }
        SEGMENT_TYPE_DECLARATIVE_FUNCREFS -> {

            val elemKind = elementKindDecoder(reader).bind()
            val refType = elemKind.let { kind ->
                when (kind) {
                    ElementKind.FuncRef -> ReferenceType.Funcref
                }
            }

            val indices = functionIndexVectorDecoder(reader, functionIndexDecoder).bind()
            val instructions: List<Instruction> = indices.vector.map { idx ->
                ReferenceInstruction.RefFunc(idx)
            }
            val expression = Expression(instructions)

            val mode = ElementSegment.Mode.Declarative

            ElementSegment(elemIdx, refType, listOf(expression), mode)
        }
        SEGMENT_TYPE_ACTIVE_NO_TABLE_MANY_EXPRESSIONS -> {

            val offSetExpression = expressionDecoder(reader).bind()
            val mode = ElementSegment.Mode.Active(Index.TableIndex(0u), offSetExpression)

            val expressions = expressionVectorDecoder(reader, expressionDecoder).bind()

            ElementSegment(elemIdx, ReferenceType.Funcref, expressions.vector, mode)
        }
        SEGMENT_TYPE_PASSIVE_MANY_EXPRESSIONS -> {

            val referenceType = referenceTypeDecoder(reader.ubyte().bind()).bind()
            val expressions = expressionVectorDecoder(reader, expressionDecoder).bind()

            ElementSegment(elemIdx, referenceType, expressions.vector, ElementSegment.Mode.Passive)
        }
        SEGMENT_TYPE_ACTIVE_W_TABLEIDX -> {

            val tableIndex = tableIndexDecoder(reader).bind()
            val offSetExpression = expressionDecoder(reader).bind()
            val mode = ElementSegment.Mode.Active(tableIndex, offSetExpression)

            val referenceType = referenceTypeDecoder(reader.ubyte().bind()).bind()
            val expressions = expressionVectorDecoder(reader, expressionDecoder).bind()

            ElementSegment(elemIdx, referenceType, expressions.vector, mode)
        }
        SEGMENT_TYPE_DECLARATIVE_MANY_EXPRESSIONS -> {

            val referenceType = referenceTypeDecoder(reader.ubyte().bind()).bind()
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
