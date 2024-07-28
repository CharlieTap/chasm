package io.github.charlietap.chasm.decoder.decoder.instruction.prefix

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.instruction.PREFIX_FB
import io.github.charlietap.chasm.decoder.decoder.instruction.PREFIX_FC
import io.github.charlietap.chasm.decoder.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun PrefixInstructionDecoder(
    context: DecoderContext,
): Result<Instruction, WasmDecodeError> =
    PrefixInstructionDecoder(
        context = context,
        prefixFBInstructionDecoder = ::PrefixFBInstructionDecoder,
        prefixFCInstructionDecoder = ::PrefixFCInstructionDecoder,
    )

internal fun PrefixInstructionDecoder(
    context: DecoderContext,
    prefixFBInstructionDecoder: Decoder<Instruction>,
    prefixFCInstructionDecoder: Decoder<Instruction>,
): Result<Instruction, WasmDecodeError> = binding {

    val prefix = context.reader.ubyte().bind()
    val opcode = context.reader.peek().uint().bind()

    when (prefix) {
        PREFIX_FB -> prefixFBInstructionDecoder(context).bind()
        PREFIX_FC -> prefixFCInstructionDecoder(context).bind()
        else -> Err(InstructionDecodeError.InvalidPrefixInstruction(prefix, opcode)).bind<Instruction>()
    }
}
