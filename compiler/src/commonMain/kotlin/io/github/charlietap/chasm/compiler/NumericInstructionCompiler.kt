package io.github.charlietap.chasm.compiler

import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.compiler.context.FunctionCompilationContext
import io.github.charlietap.chasm.compiler.instruction.emitCopy
import io.github.charlietap.chasm.compiler.instruction.emitI64WideInstruction
import io.github.charlietap.chasm.compiler.instruction.emitNumericInstruction
import io.github.charlietap.chasm.compiler.instruction.inputArity
import io.github.charlietap.chasm.compiler.instruction.isBitcast
import io.github.charlietap.chasm.compiler.instruction.resultArity
import io.github.charlietap.chasm.compiler.instruction.singleResultType
import io.github.charlietap.chasm.compiler.operand.Operand
import io.github.charlietap.chasm.compiler.operand.OperandSource
import io.github.charlietap.chasm.compiler.operand.isImmediate
import io.github.charlietap.chasm.compiler.operand.sourceSlot

internal fun compileNumericInstruction(
    state: FunctionCompilationContext,
    instruction: NumericInstruction.Operator,
    nextInstruction: Instruction?,
): Boolean {
    val opcode = instruction.opcode
    if (opcode.isBitcast) return compileBitcastInstruction(state, instruction, nextInstruction)
    if (opcode.resultArity == 2) {
        compileWideNumericInstruction(state, instruction)
        return false
    }
    val right = state.pop()
    val inputArity = opcode.inputArity
    val left = when (inputArity) {
        1 -> right
        2 -> state.pop()
        else -> error("unexpected numeric input arity: opcode=$opcode arity=$inputArity")
    }

    val destination = destination(state, left, nextInstruction)
    state.emitNumericInstruction(
        opcode = opcode,
        first = left,
        second = right,
        destinationSlot = destination.slot,
    )
    completeDestination(state, opcode.singleResultType, destination)
    return destination.consumesNextInstruction
}

private fun compileBitcastInstruction(
    state: FunctionCompilationContext,
    instruction: NumericInstruction.Operator,
    nextInstruction: Instruction?,
): Boolean {
    val operand = state.pop()
    val destination = destination(state, operand, nextInstruction)
    if (operand.isImmediate) {
        emitOperand(state, operand, destination.slot)
    } else {
        state.emitCopy(operand.sourceSlot, destination.slot)
    }
    completeDestination(state, instruction.opcode.singleResultType, destination)
    return destination.consumesNextInstruction
}

private fun compileWideNumericInstruction(
    state: FunctionCompilationContext,
    instruction: NumericInstruction.Operator,
) {
    val opcode = instruction.opcode
    val fourthOperand = if (opcode.inputArity == 4) state.pop() else null
    val thirdOperand = if (opcode.inputArity == 4) state.pop() else null
    val secondOperand = state.pop()
    val firstOperand = state.pop()
    val destinationLowSlot = state.frame.allocate()
    val destinationHighSlot = state.frame.allocate()

    state.emitI64WideInstruction(
        opcode = opcode,
        first = firstOperand,
        second = secondOperand,
        third = thirdOperand,
        fourth = fourthOperand,
        destinationLowSlot = destinationLowSlot,
        destinationHighSlot = destinationHighSlot,
    )
    state.pushFrame(I64_TYPE, destinationLowSlot)
    state.pushFrame(I64_TYPE, destinationHighSlot)
}
