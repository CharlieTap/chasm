package io.github.charlietap.chasm.optimiser.ext

import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.ir.instruction.FusedControlInstruction
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.ir.module.Function

fun Function.traverseInstructions(): Sequence<Instruction> = sequence {
    function(this@traverseInstructions.body.instructions)
}

private suspend fun SequenceScope<Instruction>.function(
    instructions: List<Instruction>,
) {
    instructions.forEach { instruction ->
        yield(instruction)

        when (instruction) {
            is ControlInstruction.Block -> function(instruction.instructions)
            is ControlInstruction.Loop -> function(instruction.instructions)
            is ControlInstruction.If -> {
                function(instruction.thenInstructions)
                instruction.elseInstructions?.let { function(it) }
            }
            is ControlInstruction.TryTable -> function(instruction.instructions)
            is FusedControlInstruction.If -> {
                function(instruction.thenInstructions)
                instruction.elseInstructions?.let { function(it) }
            }
            else -> Unit
        }
    }
}
