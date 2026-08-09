package io.github.charlietap.chasm.decoder.builder

import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Instruction

internal class InstructionBlockBuilder {

    private val instructions = mutableListOf<Instruction>()

    fun append(instruction: Instruction) {
        instructions += instruction
    }

    fun appendEnd(count: Int) {
        instructions += ControlInstruction.End(count)
    }

    fun build(): List<Instruction> = instructions
}
