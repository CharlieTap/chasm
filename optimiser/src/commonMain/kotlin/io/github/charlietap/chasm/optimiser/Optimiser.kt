package io.github.charlietap.chasm.optimiser

import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.optimiser.passes.FusionPass
import io.github.charlietap.chasm.optimiser.passes.Pass

typealias Optimiser = (List<Instruction>) -> List<Instruction>

fun Optimiser(
    instructions: List<Instruction>,
): List<Instruction> =
    Optimiser(
        instructions = instructions,
        fuser = ::FusionPass,
    )

internal fun Optimiser(
    instructions: List<Instruction>,
    fuser: Pass,
): List<Instruction> {
    return fuser(instructions)
}
