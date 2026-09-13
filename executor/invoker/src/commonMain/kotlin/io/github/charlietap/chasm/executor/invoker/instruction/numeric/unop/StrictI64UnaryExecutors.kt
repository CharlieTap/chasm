package io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun I64ClzExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64ClzI,
) = executeI64UnaryI(vstack, instruction.destinationSlot, instruction.operand, ::valueI64Clz)

internal inline fun I64ClzExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64ClzS,
) = executeI64UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot, ::valueI64Clz)

internal inline fun I64CtzExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64CtzI,
) = executeI64UnaryI(vstack, instruction.destinationSlot, instruction.operand, ::valueI64Ctz)

internal inline fun I64CtzExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64CtzS,
) = executeI64UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot, ::valueI64Ctz)

internal inline fun I64PopcntExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64PopcntI,
) = executeI64UnaryI(vstack, instruction.destinationSlot, instruction.operand, ::valueI64Popcnt)

internal inline fun I64PopcntExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64PopcntS,
) = executeI64UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot, ::valueI64Popcnt)

internal inline fun I64Extend8SExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64Extend8SI,
) = executeI64UnaryI(vstack, instruction.destinationSlot, instruction.operand, ::valueI64Extend8S)

internal inline fun I64Extend8SExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64Extend8SS,
) = executeI64UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot, ::valueI64Extend8S)

internal inline fun I64Extend16SExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64Extend16SI,
) = executeI64UnaryI(vstack, instruction.destinationSlot, instruction.operand, ::valueI64Extend16S)

internal inline fun I64Extend16SExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64Extend16SS,
) = executeI64UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot, ::valueI64Extend16S)

internal inline fun I64Extend32SExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64Extend32SI,
) = executeI64UnaryI(vstack, instruction.destinationSlot, instruction.operand, ::valueI64Extend32S)

internal inline fun I64Extend32SExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64Extend32SS,
) = executeI64UnaryS(vstack, instruction.destinationSlot, instruction.operandSlot, ::valueI64Extend32S)
