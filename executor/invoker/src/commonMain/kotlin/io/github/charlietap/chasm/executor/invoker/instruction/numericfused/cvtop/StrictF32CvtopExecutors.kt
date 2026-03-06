package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.convertF32s
import io.github.charlietap.chasm.executor.invoker.ext.convertF32u
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun F32ConvertI32SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32ConvertI32SI,
) = executeI32ToF32I(vstack, instruction.destinationSlot, instruction.operand, Int::convertF32s)

internal inline fun F32ConvertI32SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32ConvertI32SS,
) = executeI32ToF32S(vstack, instruction.destinationSlot, instruction.operandSlot, Int::convertF32s)

internal inline fun F32ConvertI32UExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32ConvertI32UI,
) = executeI32ToF32I(vstack, instruction.destinationSlot, instruction.operand, Int::convertF32u)

internal inline fun F32ConvertI32UExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32ConvertI32US,
) = executeI32ToF32S(vstack, instruction.destinationSlot, instruction.operandSlot, Int::convertF32u)

internal inline fun F32ConvertI64SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32ConvertI64SI,
) = executeI64ToF32I(vstack, instruction.destinationSlot, instruction.operand, Long::convertF32s)

internal inline fun F32ConvertI64SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32ConvertI64SS,
) = executeI64ToF32S(vstack, instruction.destinationSlot, instruction.operandSlot, Long::convertF32s)

internal inline fun F32ConvertI64UExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32ConvertI64UI,
) = executeI64ToF32I(vstack, instruction.destinationSlot, instruction.operand, Long::convertF32u)

internal inline fun F32ConvertI64UExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32ConvertI64US,
) = executeI64ToF32S(vstack, instruction.destinationSlot, instruction.operandSlot, Long::convertF32u)

internal inline fun F32DemoteF64Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32DemoteF64I,
) = executeF64ToF32I(vstack, instruction.destinationSlot, instruction.operand) { operand -> operand.toFloat() }

internal inline fun F32DemoteF64Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32DemoteF64S,
) = executeF64ToF32S(vstack, instruction.destinationSlot, instruction.operandSlot) { operand -> operand.toFloat() }

internal inline fun F32ReinterpretI32Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32ReinterpretI32I,
) = executeI32BitsToF32I(vstack, instruction.destinationSlot, instruction.operand)

internal inline fun F32ReinterpretI32Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32ReinterpretI32S,
) = executeI32BitsToF32S(vstack, instruction.destinationSlot, instruction.operandSlot)
