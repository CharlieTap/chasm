package io.github.charlietap.chasm.decoder.wasm.decoder.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.BinaryLabelIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.LabelIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.heap.BinaryHeapTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.heap.HeapTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryPrefixedControlInstructionDecoder(
    reader: WasmBinaryReader,
): Result<Instruction, WasmDecodeError> =
    BinaryPrefixedControlInstructionDecoder(
        reader = reader,
        labelIndexDecoder = ::BinaryLabelIndexDecoder,
        castFlagsDecoder = ::BinaryCastFlagsDecoder,
        heapTypeDecoder = ::BinaryHeapTypeDecoder,
    )

internal fun BinaryPrefixedControlInstructionDecoder(
    reader: WasmBinaryReader,
    labelIndexDecoder: LabelIndexDecoder,
    castFlagsDecoder: CastFlagsDecoder,
    heapTypeDecoder: HeapTypeDecoder,
): Result<Instruction, WasmDecodeError> = binding {
    when (val instruction = reader.uint().bind()) {
        BR_ON_CAST,
        BR_ON_CAST_FAIL,
        -> {
            val castFlags = castFlagsDecoder(reader).bind()
            val labelIndex = labelIndexDecoder(reader).bind()
            val srcHeapType = heapTypeDecoder(reader).bind()
            val dstHeapType = heapTypeDecoder(reader).bind()

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

            if (instruction == BR_ON_CAST) {
                ControlInstruction.BrOnCast(labelIndex, srcReferenceType, dstReferenceType)
            } else {
                ControlInstruction.BrOnCastFail(labelIndex, srcReferenceType, dstReferenceType)
            }
        }

        else -> Err(InstructionDecodeError.InvalidPrefixedControlInstruction(instruction.toUByte())).bind<Instruction>()
    }
}

internal const val BR_ON_CAST = 24u
internal const val BR_ON_CAST_FAIL = 25u
