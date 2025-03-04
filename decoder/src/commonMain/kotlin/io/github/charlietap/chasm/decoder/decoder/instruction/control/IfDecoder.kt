package io.github.charlietap.chasm.decoder.decoder.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.instruction.ELSE
import io.github.charlietap.chasm.decoder.decoder.instruction.END
import io.github.charlietap.chasm.decoder.decoder.instruction.InstructionDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun IfDecoder(
    context: DecoderContext,
): Result<Pair<List<Instruction>, List<Instruction>?>, WasmDecodeError> = IfDecoder(
    context = context,
    instructionDecoder = ::InstructionDecoder,
)

internal inline fun IfDecoder(
    context: DecoderContext,
    crossinline instructionDecoder: Decoder<Instruction>,
): Result<Pair<List<Instruction>, List<Instruction>?>, WasmDecodeError> = binding {
    val instructions = mutableListOf<Instruction>()
    var elseInstructions: MutableList<Instruction>? = null

    var activeInstructions = instructions
    do {
        val opcode = context.reader
            .peek()
            .ubyte()
            .bind()
        if (opcode != ELSE) {
            if (opcode != END) {
                activeInstructions += instructionDecoder(context).bind()
            } else {
                context.reader.ubyte().bind() // consume end byte
            }
        } else {
            context.reader.ubyte().bind() // consume else byte
            elseInstructions = mutableListOf()
            activeInstructions = elseInstructions
        }
    } while (opcode != END)

    instructions to elseInstructions
}
