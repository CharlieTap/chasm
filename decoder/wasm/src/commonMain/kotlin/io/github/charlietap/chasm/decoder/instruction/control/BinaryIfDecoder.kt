package io.github.charlietap.chasm.decoder.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.decoder.instruction.BinaryInstructionDecoder
import io.github.charlietap.chasm.decoder.instruction.ELSE
import io.github.charlietap.chasm.decoder.instruction.END
import io.github.charlietap.chasm.decoder.instruction.InstructionDecoder
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

fun BinaryIfDecoder(
    reader: WasmBinaryReader,
): Result<Pair<List<Instruction>, List<Instruction>?>, WasmDecodeError> = BinaryIfDecoder(
    reader,
    ::BinaryInstructionDecoder,
)

internal fun BinaryIfDecoder(
    reader: WasmBinaryReader,
    instructionDecoder: InstructionDecoder,
): Result<Pair<List<Instruction>, List<Instruction>?>, WasmDecodeError> = binding {
    val instructions = mutableListOf<Instruction>()
    var elseInstructions: MutableList<Instruction>? = null

    var activeInstructions = instructions
    do {
        val opcode = reader.ubyte().bind()
        if (opcode != ELSE) {
            if (opcode != END) {
                activeInstructions += instructionDecoder(reader, opcode).bind()
            }
        } else {
            elseInstructions = mutableListOf()
            activeInstructions = elseInstructions
        }
    } while (opcode != END)

    instructions to elseInstructions
}
