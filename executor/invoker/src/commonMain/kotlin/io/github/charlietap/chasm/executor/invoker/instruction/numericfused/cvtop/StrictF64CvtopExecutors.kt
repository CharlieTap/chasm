package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.convertF64s
import io.github.charlietap.chasm.executor.invoker.ext.convertF64u
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun F64ConvertI32SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64ConvertI32SI,
) = executeI32ToF64I(vstack, instruction.destinationSlot, instruction.operand, Int::convertF64s)

internal inline fun F64ConvertI32SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64ConvertI32SS,
) = executeI32ToF64S(vstack, instruction.destinationSlot, instruction.operandSlot, Int::convertF64s)

internal inline fun F64ConvertI32UExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64ConvertI32UI,
) = executeI32ToF64I(vstack, instruction.destinationSlot, instruction.operand, Int::convertF64u)

internal inline fun F64ConvertI32UExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64ConvertI32US,
) = executeI32ToF64S(vstack, instruction.destinationSlot, instruction.operandSlot, Int::convertF64u)

internal inline fun F64ConvertI64SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64ConvertI64SI,
) = executeI64ToF64I(vstack, instruction.destinationSlot, instruction.operand, Long::convertF64s)

internal inline fun F64ConvertI64SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64ConvertI64SS,
) = executeI64ToF64S(vstack, instruction.destinationSlot, instruction.operandSlot, Long::convertF64s)

internal inline fun F64ConvertI64UExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64ConvertI64UI,
) = executeI64ToF64I(vstack, instruction.destinationSlot, instruction.operand, Long::convertF64u)

internal inline fun F64ConvertI64UExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64ConvertI64US,
) = executeI64ToF64S(vstack, instruction.destinationSlot, instruction.operandSlot, Long::convertF64u)

internal inline fun F64PromoteF32Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64PromoteF32I,
) = executeF32ToF64I(vstack, instruction.destinationSlot, instruction.operand) { operand -> operand.toDouble() }

internal inline fun F64PromoteF32Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64PromoteF32S,
) = executeF32ToF64S(vstack, instruction.destinationSlot, instruction.operandSlot) { operand -> operand.toDouble() }

internal inline fun F64ReinterpretI64Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64ReinterpretI64I,
) = executeI64BitsToF64I(vstack, instruction.destinationSlot, instruction.operand)

internal inline fun F64ReinterpretI64Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64ReinterpretI64S,
) = executeI64BitsToF64S(vstack, instruction.destinationSlot, instruction.operandSlot)
