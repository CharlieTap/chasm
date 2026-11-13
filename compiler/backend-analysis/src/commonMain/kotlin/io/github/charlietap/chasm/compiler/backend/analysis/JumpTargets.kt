package io.github.charlietap.chasm.compiler.backend.analysis

import io.github.charlietap.chasm.cir.AdminInstruction
import io.github.charlietap.chasm.cir.AggregateInstruction
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.cir.Instruction
import io.github.charlietap.chasm.cir.MemoryInstruction
import io.github.charlietap.chasm.cir.NumericInstruction
import io.github.charlietap.chasm.cir.ParametricInstruction
import io.github.charlietap.chasm.cir.ReferenceInstruction
import io.github.charlietap.chasm.cir.TableInstruction
import io.github.charlietap.chasm.cir.VariableInstruction

fun CirFunction.jumpTargets(): BooleanArray {
    val targets = BooleanArray(instructions.size + 1)
    targets[0] = true
    for (instruction in instructions) {
        for (target in instruction.jumpTargets()) targets[target] = true
    }
    exceptions?.regions?.forEach { region -> region.catches.forEach { handler -> targets[handler.target.index] = true } }
    return targets
}

fun Instruction.jumpTargets(): IntArray {

    if (this is AdminInstruction.JumpTableS) {
        return targets
    }

    val target = jumpTarget()
    return if (target < 0) NO_TARGETS else intArrayOf(target)
}

internal fun Instruction.jumpTarget(): Int {

    if (this !is AdminInstruction) {
        return NO_TARGET
    }

    return when (this) {
        is AdminInstruction.Jump -> target.index
        is AdminInstruction.JumpCopies -> target.index
        is AdminInstruction.JumpIfI -> target.index
        is AdminInstruction.JumpIfS -> target.index
        is AdminInstruction.JumpIfZeroI -> target.index
        is AdminInstruction.JumpIfZeroS -> target.index
        is AdminInstruction.JumpIfCopyI -> target.index
        is AdminInstruction.JumpIfCopyS -> target.index
        is AdminInstruction.JumpIfCondition -> target.index
        is AdminInstruction.JumpIfConditionMismatch -> target.index
        is AdminInstruction.JumpOnNullI -> target.index
        is AdminInstruction.JumpOnNullS -> target.index
        is AdminInstruction.JumpOnNonNullI -> target.index
        is AdminInstruction.JumpOnNonNullS -> target.index
        is AdminInstruction.JumpOnCastI -> target.index
        is AdminInstruction.JumpOnCastS -> target.index
        is AdminInstruction.JumpOnCastFailI -> target.index
        is AdminInstruction.JumpOnCastFailS -> target.index
        else -> NO_TARGET
    }
}

private val NO_TARGETS = IntArray(0)

private const val NO_TARGET = -1

fun Instruction.fallsThrough(): Boolean {
    return when (this) {
        is AdminInstruction -> fallsThrough()
        is ControlInstruction -> fallsThrough()
        is AggregateInstruction,
        is MemoryInstruction,
        is NumericInstruction,
        is ParametricInstruction,
        is ReferenceInstruction,
        is TableInstruction,
        is VariableInstruction,
        -> true
    }
}

private fun AdminInstruction.fallsThrough(): Boolean {
    return when (this) {
        is AdminInstruction.EndFunction,
        is AdminInstruction.Jump,
        is AdminInstruction.JumpCopies,
        is AdminInstruction.JumpTableS,
        -> false
        is AdminInstruction.Checkpoint,
        is AdminInstruction.CopySlot,
        is AdminInstruction.CopySlots,
        is AdminInstruction.CopySlotSequence,
        is AdminInstruction.JumpIfI,
        is AdminInstruction.JumpIfS,
        is AdminInstruction.JumpIfZeroI,
        is AdminInstruction.JumpIfZeroS,
        is AdminInstruction.JumpIfCopyI,
        is AdminInstruction.JumpIfCopyS,
        is AdminInstruction.JumpIfCondition,
        is AdminInstruction.JumpIfConditionMismatch,
        is AdminInstruction.JumpOnNullI,
        is AdminInstruction.JumpOnNullS,
        is AdminInstruction.JumpOnNonNullI,
        is AdminInstruction.JumpOnNonNullS,
        is AdminInstruction.JumpOnCastI,
        is AdminInstruction.JumpOnCastS,
        is AdminInstruction.JumpOnCastFailI,
        is AdminInstruction.JumpOnCastFailS,
        -> true
    }
}

private fun ControlInstruction.fallsThrough(): Boolean {
    return when (this) {
        is ControlInstruction.FunctionReturn,
        is ControlInstruction.ReturnCall,
        is ControlInstruction.ReturnCallIndirectI,
        is ControlInstruction.ReturnCallIndirectS,
        is ControlInstruction.ReturnCallRefS,
        is ControlInstruction.Throw,
        is ControlInstruction.ThrowRefS,
        is ControlInstruction.Unreachable,
        -> false
        is ControlInstruction.Call,
        is ControlInstruction.CallIndirectI,
        is ControlInstruction.CallIndirectS,
        is ControlInstruction.CallRefS,
        -> true
    }
}
