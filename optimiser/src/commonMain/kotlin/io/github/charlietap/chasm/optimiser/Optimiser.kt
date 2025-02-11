package io.github.charlietap.chasm.optimiser

import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.optimiser.passes.InstructionFuser

typealias Optimiser = (List<Instruction>) -> List<Instruction>

fun Optimiser(
    instructions: List<Instruction>,
): List<Instruction> =
    Optimiser(
        instructions = instructions,
        fuser = ::InstructionFuser,
    )

internal fun Optimiser(
    instructions: List<Instruction>,
    fuser: InstructionFuser,
): List<Instruction> {
    return fuser(instructions)
}
