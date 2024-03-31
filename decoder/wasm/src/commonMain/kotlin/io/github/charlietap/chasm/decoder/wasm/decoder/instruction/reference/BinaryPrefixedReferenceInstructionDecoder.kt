package io.github.charlietap.chasm.decoder.wasm.decoder.instruction.reference

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.BinaryDataIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.BinaryElementIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.BinaryFieldIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.BinaryTypeIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.DataIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.ElementIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.FieldIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.TypeIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.heap.BinaryHeapTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.heap.HeapTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryPrefixedReferenceInstructionDecoder(
    reader: WasmBinaryReader,
): Result<Instruction, WasmDecodeError> =
    BinaryPrefixedReferenceInstructionDecoder(
        reader = reader,
        dataIndexDecoder = ::BinaryDataIndexDecoder,
        elementIndexDecoder = ::BinaryElementIndexDecoder,
        fieldIndexDecoder = ::BinaryFieldIndexDecoder,
        typeIndexDecoder = ::BinaryTypeIndexDecoder,
        heapTypeDecoder = ::BinaryHeapTypeDecoder,
    )

internal fun BinaryPrefixedReferenceInstructionDecoder(
    reader: WasmBinaryReader,
    dataIndexDecoder: DataIndexDecoder,
    elementIndexDecoder: ElementIndexDecoder,
    fieldIndexDecoder: FieldIndexDecoder,
    typeIndexDecoder: TypeIndexDecoder,
    heapTypeDecoder: HeapTypeDecoder,
): Result<Instruction, WasmDecodeError> = binding {
    when (val instruction = reader.uint().bind()) {
        STRUCT_NEW -> {
            val typeIndex = typeIndexDecoder(reader).bind()
            AggregateInstruction.StructNew(typeIndex)
        }
        STRUCT_NEW_DEFAULT -> {
            val typeIndex = typeIndexDecoder(reader).bind()
            AggregateInstruction.StructNewDefault(typeIndex)
        }
        STRUCT_GET -> {
            val typeIndex = typeIndexDecoder(reader).bind()
            val fieldIndex = fieldIndexDecoder(reader).bind()
            AggregateInstruction.StructGet(typeIndex, fieldIndex)
        }
        STRUCT_GET_SIGNED -> {
            val typeIndex = typeIndexDecoder(reader).bind()
            val fieldIndex = fieldIndexDecoder(reader).bind()
            AggregateInstruction.StructGetSigned(typeIndex, fieldIndex)
        }
        STRUCT_GET_UNSIGNED -> {
            val typeIndex = typeIndexDecoder(reader).bind()
            val fieldIndex = fieldIndexDecoder(reader).bind()
            AggregateInstruction.StructGetUnsigned(typeIndex, fieldIndex)
        }
        STRUCT_SET -> {
            val typeIndex = typeIndexDecoder(reader).bind()
            val fieldIndex = fieldIndexDecoder(reader).bind()
            AggregateInstruction.StructSet(typeIndex, fieldIndex)
        }
        ARRAY_NEW -> {
            val typeIndex = typeIndexDecoder(reader).bind()
            AggregateInstruction.ArrayNew(typeIndex)
        }
        ARRAY_NEW_DEFAULT -> {
            val typeIndex = typeIndexDecoder(reader).bind()
            AggregateInstruction.ArrayNewDefault(typeIndex)
        }
        ARRAY_NEW_FIXED -> {
            val typeIndex = typeIndexDecoder(reader).bind()
            val size = reader.uint().bind()
            AggregateInstruction.ArrayNewFixed(typeIndex, size)
        }
        ARRAY_NEW_DATA -> {
            val typeIndex = typeIndexDecoder(reader).bind()
            val dataIndex = dataIndexDecoder(reader).bind()
            AggregateInstruction.ArrayNewData(typeIndex, dataIndex)
        }
        ARRAY_NEW_ELEMENT -> {
            val typeIndex = typeIndexDecoder(reader).bind()
            val elementIndex = elementIndexDecoder(reader).bind()
            AggregateInstruction.ArrayNewElement(typeIndex, elementIndex)
        }
        ARRAY_GET -> {
            val typeIndex = typeIndexDecoder(reader).bind()
            AggregateInstruction.ArrayGet(typeIndex)
        }
        ARRAY_GET_SIGNED -> {
            val typeIndex = typeIndexDecoder(reader).bind()
            AggregateInstruction.ArrayGetSigned(typeIndex)
        }
        ARRAY_GET_UNSIGNED -> {
            val typeIndex = typeIndexDecoder(reader).bind()
            AggregateInstruction.ArrayGetUnsigned(typeIndex)
        }
        ARRAY_SET -> {
            val typeIndex = typeIndexDecoder(reader).bind()
            AggregateInstruction.ArraySet(typeIndex)
        }
        ARRAY_LEN -> AggregateInstruction.ArrayLen
        ARRAY_FILL -> {
            val typeIndex = typeIndexDecoder(reader).bind()
            AggregateInstruction.ArrayFill(typeIndex)
        }
        ARRAY_COPY -> {
            val srcTypeIndex = typeIndexDecoder(reader).bind()
            val dstTypeIndex = typeIndexDecoder(reader).bind()
            AggregateInstruction.ArrayCopy(srcTypeIndex, dstTypeIndex)
        }
        ARRAY_INIT_DATA -> {
            val typeIndex = typeIndexDecoder(reader).bind()
            val dataIndex = dataIndexDecoder(reader).bind()
            AggregateInstruction.ArrayInitData(typeIndex, dataIndex)
        }
        ARRAY_INIT_ELEMENT -> {
            val typeIndex = typeIndexDecoder(reader).bind()
            val elementIndex = elementIndexDecoder(reader).bind()
            AggregateInstruction.ArrayInitElement(typeIndex, elementIndex)
        }
        REF_TEST -> {
            val heapType = heapTypeDecoder(reader).bind()
            val referenceType = ReferenceType.Ref(heapType)
            ReferenceInstruction.RefTest(referenceType)
        }
        REF_TEST_NULL -> {
            val heapType = heapTypeDecoder(reader).bind()
            val referenceType = ReferenceType.RefNull(heapType)
            ReferenceInstruction.RefTest(referenceType)
        }
        REF_CAST -> {
            val heapType = heapTypeDecoder(reader).bind()
            val referenceType = ReferenceType.Ref(heapType)
            ReferenceInstruction.RefCast(referenceType)
        }
        REF_CAST_NULL -> {
            val heapType = heapTypeDecoder(reader).bind()
            val referenceType = ReferenceType.RefNull(heapType)
            ReferenceInstruction.RefCast(referenceType)
        }
        ANY_CONVERT_EXTERN -> AggregateInstruction.AnyConvertExtern
        EXTERN_CONVERT_ANY -> AggregateInstruction.ExternConvertAny
        REF_I31 -> AggregateInstruction.RefI31
        I31_GET_SIGNED -> AggregateInstruction.I31GetSigned
        I31_GET_UNSIGNED -> AggregateInstruction.I31GetUnsigned
        else -> Err(InstructionDecodeError.InvalidPrefixedReferenceInstruction(instruction.toUByte())).bind<Instruction>()
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
internal const val ANY_CONVERT_EXTERN = 26u
internal const val EXTERN_CONVERT_ANY = 27u
internal const val REF_I31 = 28u
internal const val I31_GET_SIGNED = 29u
internal const val I31_GET_UNSIGNED = 30u
