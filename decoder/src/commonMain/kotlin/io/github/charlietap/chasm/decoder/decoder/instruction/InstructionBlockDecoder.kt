package io.github.charlietap.chasm.decoder.decoder.instruction

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun InstructionBlockDecoder(
    context: DecoderContext,
): Result<List<Instruction>, WasmDecodeError> = InstructionBlockDecoder(
    context,
    ::InstructionDecoder,
)

internal inline fun InstructionBlockDecoder(
    context: DecoderContext,
    crossinline instructionDecoder: Decoder<Instruction>,
): Result<List<Instruction>, WasmDecodeError> = binding {
    val instructions = mutableListOf<Instruction>()
    do {
        val opcode = context.reader
            .peek()
            .ubyte()
            .bind()
        if (opcode != context.blockEndOpcode) {
            instructions += instructionDecoder(context).bind()
        } else {
            context.reader.ubyte().bind() // consume end opcode
        }
    } while (opcode != context.blockEndOpcode)

    instructions
}
