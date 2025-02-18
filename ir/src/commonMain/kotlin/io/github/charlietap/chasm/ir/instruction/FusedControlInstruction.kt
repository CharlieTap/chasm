package io.github.charlietap.chasm.ir.instruction

import io.github.charlietap.chasm.ir.instruction.ControlInstruction.BlockType

sealed interface FusedControlInstruction : Instruction {

    data class If(
        val operand: FusedOperand,
        val blockType: BlockType,
        val thenInstructions: List<Instruction>,
        val elseInstructions: List<Instruction>?,
    ) : FusedControlInstruction
}
