package io.github.charlietap.chasm.decoder.wasm.decoder.instruction.prefix

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.PREFIX_FB
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.PREFIX_FC
import io.github.charlietap.chasm.decoder.wasm.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryPrefixInstructionDecoder(
    reader: WasmBinaryReader,
    prefix: UByte,
): Result<Instruction, WasmDecodeError> =
    BinaryPrefixInstructionDecoder(
        reader = reader,
        prefix = prefix,
        prefixFBInstructionDecoder = ::BinaryPrefixFBInstructionDecoder,
        prefixFCInstructionDecoder = ::BinaryPrefixFCInstructionDecoder,
    )

internal fun BinaryPrefixInstructionDecoder(
    reader: WasmBinaryReader,
    prefix: UByte,
    prefixFBInstructionDecoder: PrefixFBInstructionDecoder,
    prefixFCInstructionDecoder: PrefixFCInstructionDecoder,
): Result<Instruction, WasmDecodeError> = binding {

    val opcode = reader.uint().bind()

    when (prefix) {
        PREFIX_FB -> prefixFBInstructionDecoder(reader, opcode).bind()
        PREFIX_FC -> prefixFCInstructionDecoder(reader, opcode).bind()
        else -> Err(InstructionDecodeError.InvalidPrefixInstruction(prefix, opcode)).bind<Instruction>()
    }
}
