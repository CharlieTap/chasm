package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.truncI64s
import io.github.charlietap.chasm.executor.invoker.ext.truncI64sTrapping
import io.github.charlietap.chasm.executor.invoker.ext.truncI64u
import io.github.charlietap.chasm.executor.invoker.ext.truncI64uTrapping
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun I64TruncF32SExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64TruncF32SI,
) = executeF32ToI64I(vstack, instruction.destinationSlot, instruction.operand, ::valueI64TruncF32S)

internal inline fun I64TruncF32SExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64TruncF32SS,
) = executeF32ToI64S(vstack, instruction.destinationSlot, instruction.operandSlot, ::valueI64TruncF32S)

internal inline fun I64TruncF32UExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64TruncF32UI,
) = executeF32ToI64I(vstack, instruction.destinationSlot, instruction.operand, ::valueI64TruncF32U)

internal inline fun I64TruncF32UExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64TruncF32US,
) = executeF32ToI64S(vstack, instruction.destinationSlot, instruction.operandSlot, ::valueI64TruncF32U)

internal inline fun I64TruncF64SExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64TruncF64SI,
) = executeF64ToI64I(vstack, instruction.destinationSlot, instruction.operand, ::valueI64TruncF64S)

internal inline fun I64TruncF64SExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64TruncF64SS,
) = executeF64ToI64S(vstack, instruction.destinationSlot, instruction.operandSlot, ::valueI64TruncF64S)

internal inline fun I64TruncF64UExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64TruncF64UI,
) = executeF64ToI64I(vstack, instruction.destinationSlot, instruction.operand, ::valueI64TruncF64U)

internal inline fun I64TruncF64UExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64TruncF64US,
) = executeF64ToI64S(vstack, instruction.destinationSlot, instruction.operandSlot, ::valueI64TruncF64U)

internal inline fun I64TruncSatF32SExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64TruncSatF32SI,
) = executeF32ToI64I(vstack, instruction.destinationSlot, instruction.operand, ::valueI64TruncSatF32S)

internal inline fun I64TruncSatF32SExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64TruncSatF32SS,
) = executeF32ToI64S(vstack, instruction.destinationSlot, instruction.operandSlot, ::valueI64TruncSatF32S)

internal inline fun I64TruncSatF32UExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64TruncSatF32UI,
) = executeF32ToI64I(vstack, instruction.destinationSlot, instruction.operand, ::valueI64TruncSatF32U)

internal inline fun I64TruncSatF32UExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64TruncSatF32US,
) = executeF32ToI64S(vstack, instruction.destinationSlot, instruction.operandSlot, ::valueI64TruncSatF32U)

internal inline fun I64TruncSatF64SExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64TruncSatF64SI,
) = executeF64ToI64I(vstack, instruction.destinationSlot, instruction.operand, ::valueI64TruncSatF64S)

internal inline fun I64TruncSatF64SExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64TruncSatF64SS,
) = executeF64ToI64S(vstack, instruction.destinationSlot, instruction.operandSlot, ::valueI64TruncSatF64S)

internal inline fun I64TruncSatF64UExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64TruncSatF64UI,
) = executeF64ToI64I(vstack, instruction.destinationSlot, instruction.operand, ::valueI64TruncSatF64U)

internal inline fun I64TruncSatF64UExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64TruncSatF64US,
) = executeF64ToI64S(vstack, instruction.destinationSlot, instruction.operandSlot, ::valueI64TruncSatF64U)
