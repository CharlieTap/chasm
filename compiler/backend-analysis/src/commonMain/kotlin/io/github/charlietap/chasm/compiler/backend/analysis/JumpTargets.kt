package io.github.charlietap.chasm.compiler.backend.analysis

import io.github.charlietap.chasm.cir.AdminInstruction
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.Instruction

fun CirFunction.jumpTargets(): BooleanArray {
    val targets = BooleanArray(instructions.size + 1)
    targets[0] = true
    for (instruction in instructions) {
        for (target in instruction.jumpTargets()) targets[target] = true
    }
    exceptions?.regions?.forEach { region -> region.catches.forEach { handler -> targets[handler.target.index] = true } }
    return targets
}

fun Instruction.jumpTargets(): IntArray = when (this) {
    is AdminInstruction.Jump -> intArrayOf(target.index)
    is AdminInstruction.JumpCopies -> intArrayOf(target.index)
    is AdminInstruction.JumpIfI -> intArrayOf(target.index)
    is AdminInstruction.JumpIfS -> intArrayOf(target.index)
    is AdminInstruction.JumpIfZeroI -> intArrayOf(target.index)
    is AdminInstruction.JumpIfZeroS -> intArrayOf(target.index)
    is AdminInstruction.JumpIfCopyI -> intArrayOf(target.index)
    is AdminInstruction.JumpIfCopyS -> intArrayOf(target.index)
    is AdminInstruction.JumpIfCondition -> intArrayOf(target.index)
    is AdminInstruction.JumpIfConditionMismatch -> intArrayOf(target.index)
    is AdminInstruction.JumpTableS -> targets
    is AdminInstruction.JumpOnNullI -> intArrayOf(target.index)
    is AdminInstruction.JumpOnNullS -> intArrayOf(target.index)
    is AdminInstruction.JumpOnNonNullI -> intArrayOf(target.index)
    is AdminInstruction.JumpOnNonNullS -> intArrayOf(target.index)
    is AdminInstruction.JumpOnCastI -> intArrayOf(target.index)
    is AdminInstruction.JumpOnCastS -> intArrayOf(target.index)
    is AdminInstruction.JumpOnCastFailI -> intArrayOf(target.index)
    is AdminInstruction.JumpOnCastFailS -> intArrayOf(target.index)
    else -> NO_TARGETS
}

private val NO_TARGETS = IntArray(0)
