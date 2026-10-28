package io.github.charlietap.chasm.compiler.frontend

import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.NumericOpcode
import io.github.charlietap.chasm.compiler.frontend.context.FunctionCompilationContext
import io.github.charlietap.chasm.compiler.frontend.instruction.emitCopy
import io.github.charlietap.chasm.compiler.frontend.instruction.emitI32BitFieldExtract
import io.github.charlietap.chasm.compiler.frontend.instruction.emitI64WideInstruction
import io.github.charlietap.chasm.compiler.frontend.instruction.emitNumericInstruction
import io.github.charlietap.chasm.compiler.frontend.instruction.inputArity
import io.github.charlietap.chasm.compiler.frontend.instruction.isBitcast
import io.github.charlietap.chasm.compiler.frontend.instruction.resultArity
import io.github.charlietap.chasm.compiler.frontend.instruction.singleResultType
import io.github.charlietap.chasm.compiler.frontend.operand.Operand
import io.github.charlietap.chasm.compiler.frontend.operand.OperandSource
import io.github.charlietap.chasm.compiler.frontend.operand.OperandSourceKind
import io.github.charlietap.chasm.compiler.frontend.operand.isImmediate
import io.github.charlietap.chasm.compiler.frontend.operand.sourceSlot

internal fun compileNumericInstruction(
    state: FunctionCompilationContext,
    instruction: NumericInstruction.Operator,
) {
    val opcode = instruction.opcode
    if (opcode.isBitcast) {
        compileBitcastInstruction(state, instruction)
        return
    }
    if (opcode.resultArity == 2) {
        compileWideNumericInstruction(state, instruction)
        return
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
        return
    }

    val destination = destination(state, left)
    state.emitNumericInstruction(
        opcode = opcode,
        first = left,
        second = right,
        destinationSlot = destination.slot,
    )
    completeDestination(state, opcode.singleResultType, destination)
}

internal fun compileNumericChain(
    state: FunctionCompilationContext,
    first: NumericInstruction.Operator,
    immediate: NumericInstruction.I32Const,
    second: NumericInstruction.Operator,
): Boolean = compileBitFieldExtractChain(state, first, immediate, second) ||
    compileShiftExtensionChain(state, first, immediate, second)

private fun compileShiftExtensionChain(
    state: FunctionCompilationContext,
    firstShift: NumericInstruction.Operator,
    secondShiftAmount: NumericInstruction.I32Const,
    secondShift: NumericInstruction.Operator,
): Boolean {
    if (firstShift.opcode != NumericOpcode.I32Shl || secondShift.opcode != NumericOpcode.I32ShrS) return false
    val firstShiftAmount = state.operands.lastOrNull() ?: return false
    if (firstShiftAmount.sourceKind != OperandSourceKind.I32Immediate) return false
    val amount = firstShiftAmount.sourceBits.toInt() and 31
    if (amount != (secondShiftAmount.value and 31)) return false
    val opcode = when (amount) {
        16 -> NumericOpcode.I32Extend16S
        24 -> NumericOpcode.I32Extend8S
        else -> return false
    }

    state.instructions.consume(2)
    state.pop()
    val operand = state.pop()
    val destination = destination(state, operand)
    state.emitNumericInstruction(
        opcode = opcode,
        first = operand,
        second = operand,
        destinationSlot = destination.slot,
    )
    completeDestination(state, opcode.singleResultType, destination)
    return true
}

private fun compileBitFieldExtractChain(
    state: FunctionCompilationContext,
    shiftInstruction: NumericInstruction.Operator,
    maskInstruction: NumericInstruction.I32Const,
    andInstruction: NumericInstruction.Operator,
): Boolean {
    if (shiftInstruction.opcode != NumericOpcode.I32ShrU || andInstruction.opcode != NumericOpcode.I32And) return false
    val shiftOperand = state.operands.lastOrNull() ?: return false
    if (shiftOperand.sourceKind != OperandSourceKind.I32Immediate) return false
    val shift = shiftOperand.sourceBits.toInt() and 31

    state.instructions.consume(2)
    state.pop()
    val operand = state.pop()
    val destination = destination(state, operand)
    state.emitI32BitFieldExtract(
        operandSlot = if (operand.isImmediate) state.materialize(operand) else operand.sourceSlot,
        shift = shift,
        mask = maskInstruction.value,
        destinationSlot = destination.slot,
    )
    completeDestination(state, NumericOpcode.I32And.singleResultType, destination)
    return true
}

private fun compileBitcastInstruction(
    state: FunctionCompilationContext,
    instruction: NumericInstruction.Operator,
) {
    val operand = state.pop()
    val destination = destination(
        state = state,
        reusableOperand = operand,
        allowRootResult = false,
    )
    if (operand.isImmediate) {
        emitOperand(state, operand, destination.slot)
    } else {
        state.emitCopy(operand.sourceSlot, destination.slot)
    }
    completeDestination(state, instruction.opcode.singleResultType, destination)
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
