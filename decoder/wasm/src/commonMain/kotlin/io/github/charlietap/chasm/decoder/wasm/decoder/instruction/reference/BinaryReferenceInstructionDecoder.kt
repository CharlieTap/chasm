package io.github.charlietap.chasm.decoder.wasm.decoder.instruction.reference

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.wasm.decoder.type.heap.BinaryHeapTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.heap.HeapTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryReferenceInstructionDecoder(
    reader: WasmBinaryReader,
    opcode: UByte,
): Result<Instruction, WasmDecodeError> =
    BinaryReferenceInstructionDecoder(
        reader = reader,
        opcode = opcode,
        heapTypeDecoder = ::BinaryHeapTypeDecoder,
    )

internal fun BinaryReferenceInstructionDecoder(
    reader: WasmBinaryReader,
    opcode: UByte,
    heapTypeDecoder: HeapTypeDecoder,
): Result<Instruction, WasmDecodeError> = binding {
    when (opcode) {
        REF_NULL -> {
            val heapType = heapTypeDecoder(reader).bind()
            ReferenceInstruction.RefNull(heapType)
        }
        REF_ISNULL -> {
            ReferenceInstruction.RefIsNull
        }
        REF_FUNC -> {
            val idx = reader.uint().bind()
            ReferenceInstruction.RefFunc(Index.FunctionIndex(idx))
        }
        REF_EQ -> {
            ReferenceInstruction.RefEq
        }
        REF_AS_NON_NULL -> {
            ReferenceInstruction.RefAsNonNull
        }
        else -> Err(InstructionDecodeError.InvalidReferenceInstruction(opcode)).bind<Instruction>()
    }
}

internal const val REF_NULL: UByte = 0xD0u
internal const val REF_ISNULL: UByte = 0xD1u
internal const val REF_FUNC: UByte = 0xD2u
internal const val REF_EQ: UByte = 0xD3u
internal const val REF_AS_NON_NULL: UByte = 0xD4u
