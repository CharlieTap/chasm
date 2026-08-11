package io.github.charlietap.chasm.compiler

import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.NumericOpcode
import io.github.charlietap.chasm.compiler.context.FunctionCompilationContext
import io.github.charlietap.chasm.compiler.instruction.emitCopy
import io.github.charlietap.chasm.compiler.instruction.emitI32BitFieldExtract
import io.github.charlietap.chasm.compiler.instruction.emitI64WideInstruction
import io.github.charlietap.chasm.compiler.instruction.emitNumericInstruction
import io.github.charlietap.chasm.compiler.instruction.inputArity
import io.github.charlietap.chasm.compiler.instruction.isBitcast
import io.github.charlietap.chasm.compiler.instruction.resultArity
import io.github.charlietap.chasm.compiler.instruction.singleResultType
import io.github.charlietap.chasm.compiler.operand.Operand
import io.github.charlietap.chasm.compiler.operand.OperandSource
import io.github.charlietap.chasm.compiler.operand.OperandSourceKind
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

    if (
        opcode == NumericOpcode.I32Add &&
        left.sourceKind == OperandSourceKind.I32Immediate &&
        right.sourceKind == OperandSourceKind.I32Immediate
    ) {
        state.pushI32(opcode.singleResultType, left.reservedSlot, left.sourceBits.toInt() + right.sourceBits.toInt())
        return false
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

internal fun compileNumericChain(
    state: FunctionCompilationContext,
    first: NumericInstruction.Operator,
    immediate: NumericInstruction.I32Const,
    second: NumericInstruction.Operator,
    nextInstruction: Instruction?,
): Int? = compileBitFieldExtractChain(state, first, immediate, second, nextInstruction)
    ?: compileShiftExtensionChain(state, first, immediate, second, nextInstruction)

private fun compileShiftExtensionChain(
    state: FunctionCompilationContext,
    firstShift: NumericInstruction.Operator,
    secondShiftAmount: NumericInstruction.I32Const,
    secondShift: NumericInstruction.Operator,
    nextInstruction: Instruction?,
): Int? {
    if (firstShift.opcode != NumericOpcode.I32Shl || secondShift.opcode != NumericOpcode.I32ShrS) return null
    val firstShiftAmount = state.operands.lastOrNull() ?: return null
    if (firstShiftAmount.sourceKind != OperandSourceKind.I32Immediate) return null
    val amount = firstShiftAmount.sourceBits.toInt() and 31
    if (amount != (secondShiftAmount.value and 31)) return null
    val opcode = when (amount) {
        16 -> NumericOpcode.I32Extend16S
        24 -> NumericOpcode.I32Extend8S
        else -> return null
    }

    state.pop()
    val operand = state.pop()
    val destination = destination(state, operand, nextInstruction)
    state.emitNumericInstruction(
        opcode = opcode,
        first = operand,
        second = operand,
        destinationSlot = destination.slot,
    )
    completeDestination(state, opcode.singleResultType, destination)
    return if (destination.consumesNextInstruction) 4 else 3
}

private fun compileBitFieldExtractChain(
    state: FunctionCompilationContext,
    shiftInstruction: NumericInstruction.Operator,
    maskInstruction: NumericInstruction.I32Const,
    andInstruction: NumericInstruction.Operator,
    nextInstruction: Instruction?,
): Int? {
    if (shiftInstruction.opcode != NumericOpcode.I32ShrU || andInstruction.opcode != NumericOpcode.I32And) return null
    val shiftOperand = state.operands.lastOrNull() ?: return null
    if (shiftOperand.sourceKind != OperandSourceKind.I32Immediate) return null
    val shift = shiftOperand.sourceBits.toInt() and 31

    state.pop()
    val operand = state.pop()
    val destination = destination(state, operand, nextInstruction)
    state.emitI32BitFieldExtract(
        operandSlot = if (operand.isImmediate) state.materialize(operand) else operand.sourceSlot,
        shift = shift,
        mask = maskInstruction.value,
        destinationSlot = destination.slot,
    )
    completeDestination(state, NumericOpcode.I32And.singleResultType, destination)
    return if (destination.consumesNextInstruction) 4 else 3
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
