package io.github.charlietap.chasm.decoder.wasm.decoder.instruction

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryInstructionBlockDecoder(
    reader: WasmBinaryReader,
    blockEndOpcode: UByte,
): Result<List<Instruction>, WasmDecodeError> = BinaryInstructionBlockDecoder(
    reader,
    blockEndOpcode,
    ::BinaryInstructionDecoder,
)

internal fun BinaryInstructionBlockDecoder(
    reader: WasmBinaryReader,
    blockEndOpcode: UByte,
    instructionDecoder: InstructionDecoder,
): Result<List<Instruction>, WasmDecodeError> = binding {
    val instructions = mutableListOf<Instruction>()
    do {
        val opcode = reader.ubyte().bind()
        if (opcode != blockEndOpcode) { // TODO make this branchless
            instructions += instructionDecoder(reader, opcode).bind()
        }
    } while (opcode != blockEndOpcode)

    instructions
}
