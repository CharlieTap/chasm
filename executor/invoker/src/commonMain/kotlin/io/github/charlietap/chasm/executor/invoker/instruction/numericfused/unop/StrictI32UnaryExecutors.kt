package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun I32ClzExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I32ClzI,
) = executeI32UnaryI(vstack, instruction.destinationSlot, instruction.operand) { operand -> operand.countLeadingZeroBits() }

internal inline fun I32ClzExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I32ClzS,
) = executeI32UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot) { operand -> operand.countLeadingZeroBits() }

internal inline fun I32CtzExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I32CtzI,
) = executeI32UnaryI(vstack, instruction.destinationSlot, instruction.operand) { operand -> operand.countTrailingZeroBits() }

internal inline fun I32CtzExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I32CtzS,
) = executeI32UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot) { operand -> operand.countTrailingZeroBits() }

internal inline fun I32PopcntExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I32PopcntI,
) = executeI32UnaryI(vstack, instruction.destinationSlot, instruction.operand) { operand -> operand.countOneBits() }

internal inline fun I32PopcntExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I32PopcntS,
) = executeI32UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot) { operand -> operand.countOneBits() }

internal inline fun I32Extend8SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I32Extend8SI,
) = executeI32UnaryI(vstack, instruction.destinationSlot, instruction.operand) { operand -> (operand and 0xFF).toByte().toInt() }

internal inline fun I32Extend8SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I32Extend8SS,
) = executeI32UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot) { operand -> (operand and 0xFF).toByte().toInt() }

internal inline fun I32Extend16SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I32Extend16SI,
) = executeI32UnaryI(vstack, instruction.destinationSlot, instruction.operand) { operand -> (operand and 0xFFFF).toShort().toInt() }

internal inline fun I32Extend16SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I32Extend16SS,
) = executeI32UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot) { operand -> (operand and 0xFFFF).toShort().toInt() }
