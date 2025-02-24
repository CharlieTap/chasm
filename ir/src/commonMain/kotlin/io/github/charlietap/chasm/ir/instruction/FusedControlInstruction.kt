package io.github.charlietap.chasm.ir.instruction

import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.type.BlockType

sealed interface FusedControlInstruction : Instruction {

    data class BrIf(
        val operand: FusedOperand,
        val labelIndex: Index.LabelIndex,
    ) : FusedControlInstruction

    data class Call(
        val operands: List<FusedOperand>,
        val functionIndex: Index.FunctionIndex,
    ) : FusedControlInstruction

    data class If(
        val operand: FusedOperand,
        val blockType: BlockType,
        val thenInstructions: List<Instruction>,
        val elseInstructions: List<Instruction>?,
    ) : FusedControlInstruction
}
