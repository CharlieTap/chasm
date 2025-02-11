package io.github.charlietap.chasm.optimiser.passes

import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.ir.instruction.NumericInstruction
import io.github.charlietap.chasm.ir.instruction.VariableInstruction

typealias InstructionFuser = (List<Instruction>) -> List<Instruction>

fun InstructionFuser(
    instructions: List<Instruction>,
): List<Instruction> {

    val fused = mutableListOf<Instruction>()

    var idx = 0
    while (idx < instructions.size) {

        val instruction = instructions[idx]

        when (instruction) {
            is NumericInstruction.I32Add -> {

                val left = instructions.getOrNull(idx - 2)?.let(::operand)
                val right = instructions.getOrNull(idx - 1)?.let(::operand)

                if (left == null || right == null) {
                    fused.add(instruction)
                    idx++
                    continue
                }

                fused.removeLast()
                fused.removeLast()

                val destination = instructions.getOrNull(idx + 1).let(::destination)

                if (destination != FusedDestination.ValueStack) {
                    idx++
                }

                fused.add(
                    FusedNumericInstruction.I32Add(
                        left = left,
                        right = right,
                        destination = destination,
                    ),
                )
            }
            else -> fused.add(instruction)
        }
        idx++
    }

    return fused
}

private fun operand(
    instruction: Instruction,
): FusedOperand? = when (instruction) {
    is NumericInstruction.I32Const -> FusedOperand.I32Const(instruction.value)
    is NumericInstruction.I64Const -> FusedOperand.I64Const(instruction.value)
    is NumericInstruction.F32Const -> FusedOperand.F32Const(instruction.value)
    is NumericInstruction.F64Const -> FusedOperand.F64Const(instruction.value)
    is VariableInstruction.LocalGet -> FusedOperand.LocalGet(instruction.localIdx)
    is VariableInstruction.GlobalGet -> FusedOperand.GlobalGet(instruction.globalIdx)
    else -> null
}

private fun destination(
    instruction: Instruction?,
): FusedDestination = when (instruction) {
    is VariableInstruction.LocalSet -> FusedDestination.LocalSet(instruction.localIdx)
    is VariableInstruction.GlobalSet -> FusedDestination.GlobalSet(instruction.globalIdx)
    else -> FusedDestination.ValueStack
}
