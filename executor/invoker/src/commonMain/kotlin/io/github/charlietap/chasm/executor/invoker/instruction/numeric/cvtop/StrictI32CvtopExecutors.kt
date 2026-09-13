package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.truncI32s
import io.github.charlietap.chasm.executor.invoker.ext.truncI32sTrapping
import io.github.charlietap.chasm.executor.invoker.ext.truncI32u
import io.github.charlietap.chasm.executor.invoker.ext.truncI32uTrapping
import io.github.charlietap.chasm.executor.invoker.ext.wrap
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun I32TruncF32SExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I32TruncF32SI,
) = executeF32ToI32I(vstack, instruction.destinationSlot, instruction.operand, ::valueI32TruncF32S)

internal inline fun I32TruncF32SExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I32TruncF32SS,
) = executeF32ToI32S(vstack, instruction.destinationSlot, instruction.operandSlot, ::valueI32TruncF32S)

internal inline fun I32TruncF32UExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I32TruncF32UI,
) = executeF32ToI32I(vstack, instruction.destinationSlot, instruction.operand, ::valueI32TruncF32U)

internal inline fun I32TruncF32UExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I32TruncF32US,
) = executeF32ToI32S(vstack, instruction.destinationSlot, instruction.operandSlot, ::valueI32TruncF32U)

internal inline fun I32TruncF64SExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I32TruncF64SI,
) = executeF64ToI32I(vstack, instruction.destinationSlot, instruction.operand, ::valueI32TruncF64S)

internal inline fun I32TruncF64SExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I32TruncF64SS,
) = executeF64ToI32S(vstack, instruction.destinationSlot, instruction.operandSlot, ::valueI32TruncF64S)

internal inline fun I32TruncF64UExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I32TruncF64UI,
) = executeF64ToI32I(vstack, instruction.destinationSlot, instruction.operand, ::valueI32TruncF64U)

internal inline fun I32TruncF64UExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I32TruncF64US,
) = executeF64ToI32S(vstack, instruction.destinationSlot, instruction.operandSlot, ::valueI32TruncF64U)

internal inline fun I32TruncSatF32SExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I32TruncSatF32SI,
) = executeF32ToI32I(vstack, instruction.destinationSlot, instruction.operand, ::valueI32TruncSatF32S)

internal inline fun I32TruncSatF32SExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I32TruncSatF32SS,
) = executeF32ToI32S(vstack, instruction.destinationSlot, instruction.operandSlot, ::valueI32TruncSatF32S)

internal inline fun I32TruncSatF32UExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I32TruncSatF32UI,
) = executeF32ToI32I(vstack, instruction.destinationSlot, instruction.operand, ::valueI32TruncSatF32U)

internal inline fun I32TruncSatF32UExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I32TruncSatF32US,
) = executeF32ToI32S(vstack, instruction.destinationSlot, instruction.operandSlot, ::valueI32TruncSatF32U)

internal inline fun I32TruncSatF64SExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I32TruncSatF64SI,
) = executeF64ToI32I(vstack, instruction.destinationSlot, instruction.operand, ::valueI32TruncSatF64S)

internal inline fun I32TruncSatF64SExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I32TruncSatF64SS,
) = executeF64ToI32S(vstack, instruction.destinationSlot, instruction.operandSlot, ::valueI32TruncSatF64S)

internal inline fun I32TruncSatF64UExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I32TruncSatF64UI,
) = executeF64ToI32I(vstack, instruction.destinationSlot, instruction.operand, ::valueI32TruncSatF64U)

internal inline fun I32TruncSatF64UExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I32TruncSatF64US,
) = executeF64ToI32S(vstack, instruction.destinationSlot, instruction.operandSlot, ::valueI32TruncSatF64U)

internal inline fun I32WrapI64Executor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I32WrapI64I,
) = executeI64ToI32I(vstack, instruction.destinationSlot, instruction.operand, ::valueI32WrapI64)

internal inline fun I32WrapI64Executor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I32WrapI64S,
) = executeI64ToI32S(vstack, instruction.destinationSlot, instruction.operandSlot, ::valueI32WrapI64)
