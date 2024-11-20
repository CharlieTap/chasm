package io.github.charlietap.chasm.decoder.decoder.instruction.prefix

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.instruction.control.CastFlags
import io.github.charlietap.chasm.decoder.decoder.instruction.control.CastFlagsDecoder
import io.github.charlietap.chasm.decoder.decoder.instruction.control.Nullability
import io.github.charlietap.chasm.decoder.decoder.section.index.DataIndexDecoder
import io.github.charlietap.chasm.decoder.decoder.section.index.ElementIndexDecoder
import io.github.charlietap.chasm.decoder.decoder.section.index.FieldIndexDecoder
import io.github.charlietap.chasm.decoder.decoder.section.index.LabelIndexDecoder
import io.github.charlietap.chasm.decoder.decoder.section.index.TypeIndexDecoder
import io.github.charlietap.chasm.decoder.decoder.type.heap.HeapTypeDecoder
import io.github.charlietap.chasm.decoder.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun GCInstructionDecoder(
    context: DecoderContext,
): Result<Instruction, WasmDecodeError> =
    GCInstructionDecoder(
        context = context,
        dataIndexDecoder = ::DataIndexDecoder,
        elementIndexDecoder = ::ElementIndexDecoder,
        fieldIndexDecoder = ::FieldIndexDecoder,
        typeIndexDecoder = ::TypeIndexDecoder,
        heapTypeDecoder = ::HeapTypeDecoder,
        labelIndexDecoder = ::LabelIndexDecoder,
        castFlagsDecoder = ::CastFlagsDecoder,
    )

internal inline fun GCInstructionDecoder(
    context: DecoderContext,
    crossinline dataIndexDecoder: Decoder<Index.DataIndex>,
    crossinline elementIndexDecoder: Decoder<Index.ElementIndex>,
    crossinline fieldIndexDecoder: Decoder<Index.FieldIndex>,
    crossinline typeIndexDecoder: Decoder<Index.TypeIndex>,
    crossinline heapTypeDecoder: Decoder<HeapType>,
    crossinline labelIndexDecoder: Decoder<Index.LabelIndex>,
    crossinline castFlagsDecoder: Decoder<CastFlags>,
): Result<Instruction, WasmDecodeError> = binding {

    when (val opcode = context.reader.uint().bind()) {
        STRUCT_NEW -> {
            val typeIndex = typeIndexDecoder(context).bind()
            AggregateInstruction.StructNew(typeIndex)
        }
        STRUCT_NEW_DEFAULT -> {
            val typeIndex = typeIndexDecoder(context).bind()
            AggregateInstruction.StructNewDefault(typeIndex)
        }
        STRUCT_GET -> {
            val typeIndex = typeIndexDecoder(context).bind()
            val fieldIndex = fieldIndexDecoder(context).bind()
            AggregateInstruction.StructGet(typeIndex, fieldIndex)
        }
        STRUCT_GET_SIGNED -> {
            val typeIndex = typeIndexDecoder(context).bind()
            val fieldIndex = fieldIndexDecoder(context).bind()
            AggregateInstruction.StructGetSigned(typeIndex, fieldIndex)
        }
        STRUCT_GET_UNSIGNED -> {
            val typeIndex = typeIndexDecoder(context).bind()
            val fieldIndex = fieldIndexDecoder(context).bind()
            AggregateInstruction.StructGetUnsigned(typeIndex, fieldIndex)
        }
        STRUCT_SET -> {
            val typeIndex = typeIndexDecoder(context).bind()
            val fieldIndex = fieldIndexDecoder(context).bind()
            AggregateInstruction.StructSet(typeIndex, fieldIndex)
        }
        ARRAY_NEW -> {
            val typeIndex = typeIndexDecoder(context).bind()
            AggregateInstruction.ArrayNew(typeIndex)
        }
        ARRAY_NEW_DEFAULT -> {
            val typeIndex = typeIndexDecoder(context).bind()
            AggregateInstruction.ArrayNewDefault(typeIndex)
        }
        ARRAY_NEW_FIXED -> {
            val typeIndex = typeIndexDecoder(context).bind()
            val size = context.reader.uint().bind()
            AggregateInstruction.ArrayNewFixed(typeIndex, size)
        }
        ARRAY_NEW_DATA -> {
            val typeIndex = typeIndexDecoder(context).bind()
            val dataIndex = dataIndexDecoder(context).bind()
            AggregateInstruction.ArrayNewData(typeIndex, dataIndex)
        }
        ARRAY_NEW_ELEMENT -> {
            val typeIndex = typeIndexDecoder(context).bind()
            val elementIndex = elementIndexDecoder(context).bind()
            AggregateInstruction.ArrayNewElement(typeIndex, elementIndex)
        }
        ARRAY_GET -> {
            val typeIndex = typeIndexDecoder(context).bind()
            AggregateInstruction.ArrayGet(typeIndex)
        }
        ARRAY_GET_SIGNED -> {
            val typeIndex = typeIndexDecoder(context).bind()
            AggregateInstruction.ArrayGetSigned(typeIndex)
        }
        ARRAY_GET_UNSIGNED -> {
            val typeIndex = typeIndexDecoder(context).bind()
            AggregateInstruction.ArrayGetUnsigned(typeIndex)
        }
        ARRAY_SET -> {
            val typeIndex = typeIndexDecoder(context).bind()
            AggregateInstruction.ArraySet(typeIndex)
        }
        ARRAY_LEN -> AggregateInstruction.ArrayLen
        ARRAY_FILL -> {
            val typeIndex = typeIndexDecoder(context).bind()
            AggregateInstruction.ArrayFill(typeIndex)
        }
        ARRAY_COPY -> {
            val dstTypeIndex = typeIndexDecoder(context).bind()
            val srcTypeIndex = typeIndexDecoder(context).bind()
            AggregateInstruction.ArrayCopy(srcTypeIndex, dstTypeIndex)
        }
        ARRAY_INIT_DATA -> {
            val typeIndex = typeIndexDecoder(context).bind()
            val dataIndex = dataIndexDecoder(context).bind()
            AggregateInstruction.ArrayInitData(typeIndex, dataIndex)
        }
        ARRAY_INIT_ELEMENT -> {
            val typeIndex = typeIndexDecoder(context).bind()
            val elementIndex = elementIndexDecoder(context).bind()
            AggregateInstruction.ArrayInitElement(typeIndex, elementIndex)
        }
        REF_TEST -> {
            val heapType = heapTypeDecoder(context).bind()
            val referenceType = ReferenceType.Ref(heapType)
            ReferenceInstruction.RefTest(referenceType)
        }
        REF_TEST_NULL -> {
            val heapType = heapTypeDecoder(context).bind()
            val referenceType = ReferenceType.RefNull(heapType)
            ReferenceInstruction.RefTest(referenceType)
        }
        REF_CAST -> {
            val heapType = heapTypeDecoder(context).bind()
            val referenceType = ReferenceType.Ref(heapType)
            ReferenceInstruction.RefCast(referenceType)
        }
        REF_CAST_NULL -> {
            val heapType = heapTypeDecoder(context).bind()
            val referenceType = ReferenceType.RefNull(heapType)
            ReferenceInstruction.RefCast(referenceType)
        }
        BR_ON_CAST,
        BR_ON_CAST_FAIL,
        -> {
            val castFlags = castFlagsDecoder(context).bind()
            val labelIndex = labelIndexDecoder(context).bind()
            val srcHeapType = heapTypeDecoder(context).bind()
            val dstHeapType = heapTypeDecoder(context).bind()

            val srcReferenceType = if (castFlags.src == Nullability.NON_NULL) {
                ReferenceType.Ref(srcHeapType)
            } else {
                ReferenceType.RefNull(srcHeapType)
            }

            val dstReferenceType = if (castFlags.dst == Nullability.NON_NULL) {
                ReferenceType.Ref(dstHeapType)
            } else {
                ReferenceType.RefNull(dstHeapType)
            }

            if (opcode == BR_ON_CAST) {
                ControlInstruction.BrOnCast(labelIndex, srcReferenceType, dstReferenceType)
            } else {
                ControlInstruction.BrOnCastFail(labelIndex, srcReferenceType, dstReferenceType)
            }
        }
        ANY_CONVERT_EXTERN -> AggregateInstruction.AnyConvertExtern
        EXTERN_CONVERT_ANY -> AggregateInstruction.ExternConvertAny
        REF_I31 -> AggregateInstruction.RefI31
        I31_GET_SIGNED -> AggregateInstruction.I31GetSigned
        I31_GET_UNSIGNED -> AggregateInstruction.I31GetUnsigned

        else -> Err(InstructionDecodeError.InvalidPrefixInstruction(PREFIX_FB, opcode)).bind<Instruction>()
    }
}

internal const val STRUCT_NEW = 0u
internal const val STRUCT_NEW_DEFAULT = 1u
internal const val STRUCT_GET = 2u
internal const val STRUCT_GET_SIGNED = 3u
internal const val STRUCT_GET_UNSIGNED = 4u
internal const val STRUCT_SET = 5u
internal const val ARRAY_NEW = 6u
internal const val ARRAY_NEW_DEFAULT = 7u
internal const val ARRAY_NEW_FIXED = 8u
internal const val ARRAY_NEW_DATA = 9u
internal const val ARRAY_NEW_ELEMENT = 10u
internal const val ARRAY_GET = 11u
internal const val ARRAY_GET_SIGNED = 12u
internal const val ARRAY_GET_UNSIGNED = 13u
internal const val ARRAY_SET = 14u
internal const val ARRAY_LEN = 15u
internal const val ARRAY_FILL = 16u
internal const val ARRAY_COPY = 17u
internal const val ARRAY_INIT_DATA = 18u
internal const val ARRAY_INIT_ELEMENT = 19u
internal const val REF_TEST = 20u
internal const val REF_TEST_NULL = 21u
internal const val REF_CAST = 22u
internal const val REF_CAST_NULL = 23u
internal const val BR_ON_CAST = 24u
internal const val BR_ON_CAST_FAIL = 25u
internal const val ANY_CONVERT_EXTERN = 26u
internal const val EXTERN_CONVERT_ANY = 27u
internal const val REF_I31 = 28u
internal const val I31_GET_SIGNED = 29u
internal const val I31_GET_UNSIGNED = 30u
