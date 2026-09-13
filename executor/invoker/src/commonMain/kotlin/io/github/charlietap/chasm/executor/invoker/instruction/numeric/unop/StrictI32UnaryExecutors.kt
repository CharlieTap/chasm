package io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun I32ClzExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I32ClzI,
) = executeI32UnaryI(vstack, instruction.destinationSlot, instruction.operand, ::valueI32Clz)

internal inline fun I32ClzExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I32ClzS,
) = executeI32UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot, ::valueI32Clz)

internal inline fun I32CtzExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I32CtzI,
) = executeI32UnaryI(vstack, instruction.destinationSlot, instruction.operand, ::valueI32Ctz)

internal inline fun I32CtzExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I32CtzS,
) = executeI32UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot, ::valueI32Ctz)

internal inline fun I32PopcntExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I32PopcntI,
) = executeI32UnaryI(vstack, instruction.destinationSlot, instruction.operand, ::valueI32Popcnt)

internal inline fun I32PopcntExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I32PopcntS,
) = executeI32UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot, ::valueI32Popcnt)

internal inline fun I32Extend8SExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I32Extend8SI,
) = executeI32UnaryI(vstack, instruction.destinationSlot, instruction.operand, ::valueI32Extend8S)

internal inline fun I32Extend8SExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I32Extend8SS,
) = executeI32UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot, ::valueI32Extend8S)

internal inline fun I32Extend16SExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I32Extend16SI,
) = executeI32UnaryI(vstack, instruction.destinationSlot, instruction.operand, ::valueI32Extend16S)

internal inline fun I32Extend16SExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I32Extend16SS,
) = executeI32UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot, ::valueI32Extend16S)
