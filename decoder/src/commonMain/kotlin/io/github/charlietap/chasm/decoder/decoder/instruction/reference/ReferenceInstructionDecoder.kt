package io.github.charlietap.chasm.decoder.decoder.instruction.reference

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.type.heap.HeapTypeDecoder
import io.github.charlietap.chasm.decoder.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun ReferenceInstructionDecoder(
    context: DecoderContext,
): Result<ReferenceInstruction, WasmDecodeError> =
    ReferenceInstructionDecoder(
        context = context,
        heapTypeDecoder = ::HeapTypeDecoder,
    )

internal fun ReferenceInstructionDecoder(
    context: DecoderContext,
    heapTypeDecoder: Decoder<HeapType>,
): Result<ReferenceInstruction, WasmDecodeError> = binding {
    when (val opcode = context.reader.ubyte().bind()) {
        REF_NULL -> {
            val heapType = heapTypeDecoder(context).bind()
            ReferenceInstruction.RefNull(heapType)
        }
        REF_ISNULL -> {
            ReferenceInstruction.RefIsNull
        }
        REF_FUNC -> {
            val idx = context.reader.uint().bind()
            ReferenceInstruction.RefFunc(Index.FunctionIndex(idx))
        }
        REF_EQ -> {
            ReferenceInstruction.RefEq
        }
        REF_AS_NON_NULL -> {
            ReferenceInstruction.RefAsNonNull
        }
        else -> Err(InstructionDecodeError.InvalidReferenceInstruction(opcode)).bind<ReferenceInstruction>()
    }
}

internal const val REF_NULL: UByte = 0xD0u
internal const val REF_ISNULL: UByte = 0xD1u
internal const val REF_FUNC: UByte = 0xD2u
internal const val REF_EQ: UByte = 0xD3u
internal const val REF_AS_NON_NULL: UByte = 0xD4u
