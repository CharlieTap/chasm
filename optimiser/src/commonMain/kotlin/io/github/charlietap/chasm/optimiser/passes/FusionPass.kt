package io.github.charlietap.chasm.optimiser.passes

import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.optimiser.passes.fusion.InstructionFuser

internal fun FusionPass(
    instructions: List<Instruction>,
): List<Instruction> =
    FusionPass(
        instructions = instructions,
        fuser = ::InstructionFuser,
    )

internal fun FusionPass(
    instructions: List<Instruction>,
    fuser: InstructionFuser,
): List<Instruction> {

    val fused = mutableListOf<Instruction>()

    var idx = 0
    while (idx < instructions.size) {

        val instruction = instructions[idx]
        idx = fuser(idx, instruction, instructions, fused)

        idx++
    }

    return fused
}
