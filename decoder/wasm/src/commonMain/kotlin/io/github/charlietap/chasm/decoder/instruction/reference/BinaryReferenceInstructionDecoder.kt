package io.github.charlietap.chasm.decoder.instruction.reference

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.decoder.instruction.REF_FUNC
import io.github.charlietap.chasm.decoder.instruction.REF_ISNULL
import io.github.charlietap.chasm.decoder.instruction.REF_NULL
import io.github.charlietap.chasm.decoder.type.reference.BinaryReferenceTypeDecoder
import io.github.charlietap.chasm.decoder.type.reference.ReferenceTypeDecoder
import io.github.charlietap.chasm.error.InstructionDecodeError
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

fun BinaryReferenceInstructionDecoder(
    reader: WasmBinaryReader,
    opcode: UByte,
): Result<Instruction, WasmDecodeError> =
    BinaryReferenceInstructionDecoder(
        reader = reader,
        opcode = opcode,
        referenceTypeDecoder = ::BinaryReferenceTypeDecoder,
    )

internal fun BinaryReferenceInstructionDecoder(
    reader: WasmBinaryReader,
    opcode: UByte,
    referenceTypeDecoder: ReferenceTypeDecoder,
): Result<Instruction, WasmDecodeError> = binding {
    when (opcode) {
        REF_NULL -> {
            val refType = referenceTypeDecoder(reader.ubyte().bind()).bind()
            ReferenceInstruction.RefNull(refType)
        }
        REF_ISNULL -> {
            ReferenceInstruction.RefIsNull
        }
        REF_FUNC -> {
            val idx = reader.uint().bind()
            ReferenceInstruction.RefFunc(Index.FunctionIndex(idx))
        }
        else -> Err(InstructionDecodeError.InvalidReferenceInstruction(opcode)).bind<Instruction>()
    }
}
