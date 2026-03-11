package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.truncI32s
import io.github.charlietap.chasm.executor.invoker.ext.truncI32sTrapping
import io.github.charlietap.chasm.executor.invoker.ext.truncI32u
import io.github.charlietap.chasm.executor.invoker.ext.truncI32uTrapping
import io.github.charlietap.chasm.executor.invoker.ext.wrap
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.NumericSuperInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun I32ReinterpretF32Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32ReinterpretF32I,
) = executeF32ToI32I(vstack, instruction.destinationSlot, instruction.operand) { operand ->
    operand.toRawBits()
}

internal inline fun I32ReinterpretF32Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32ReinterpretF32S,
) = executeF32ToI32S(vstack, instruction.destinationSlot, instruction.operandSlot) { operand ->
    operand.toRawBits()
}

internal inline fun I32TruncF32SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32TruncF32SI,
) = executeF32ToI32I(vstack, instruction.destinationSlot, instruction.operand) { operand ->
    try {
        operand.truncI32sTrapping()
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.ConvertOperationFailed)
    }
}

internal inline fun I32TruncF32SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32TruncF32SS,
) = executeF32ToI32S(vstack, instruction.destinationSlot, instruction.operandSlot) { operand ->
    try {
        operand.truncI32sTrapping()
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.ConvertOperationFailed)
    }
}

internal inline fun I32TruncF32UExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32TruncF32UI,
) = executeF32ToI32I(vstack, instruction.destinationSlot, instruction.operand) { operand ->
    try {
        operand.truncI32uTrapping()
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.ConvertOperationFailed)
    }
}

internal inline fun I32TruncF32UExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32TruncF32US,
) = executeF32ToI32S(vstack, instruction.destinationSlot, instruction.operandSlot) { operand ->
    try {
        operand.truncI32uTrapping()
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.ConvertOperationFailed)
    }
}

internal inline fun I32TruncF64SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32TruncF64SI,
) = executeF64ToI32I(vstack, instruction.destinationSlot, instruction.operand) { operand ->
    try {
        operand.truncI32sTrapping()
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.ConvertOperationFailed)
    }
}

internal inline fun I32TruncF64SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32TruncF64SS,
) = executeF64ToI32S(vstack, instruction.destinationSlot, instruction.operandSlot) { operand ->
    try {
        operand.truncI32sTrapping()
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.ConvertOperationFailed)
    }
}

internal inline fun I32TruncF64UExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32TruncF64UI,
) = executeF64ToI32I(vstack, instruction.destinationSlot, instruction.operand) { operand ->
    try {
        operand.truncI32uTrapping()
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.ConvertOperationFailed)
    }
}

internal inline fun I32TruncF64UExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32TruncF64US,
) = executeF64ToI32S(vstack, instruction.destinationSlot, instruction.operandSlot) { operand ->
    try {
        operand.truncI32uTrapping()
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.ConvertOperationFailed)
    }
}

internal inline fun I32TruncSatF32SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32TruncSatF32SI,
) = executeF32ToI32I(vstack, instruction.destinationSlot, instruction.operand) { operand ->
    operand.truncI32s()
}

internal inline fun I32TruncSatF32SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32TruncSatF32SS,
) = executeF32ToI32S(vstack, instruction.destinationSlot, instruction.operandSlot) { operand ->
    operand.truncI32s()
}

internal inline fun I32TruncSatF32UExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32TruncSatF32UI,
) = executeF32ToI32I(vstack, instruction.destinationSlot, instruction.operand) { operand ->
    operand.truncI32u()
}

internal inline fun I32TruncSatF32UExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32TruncSatF32US,
) = executeF32ToI32S(vstack, instruction.destinationSlot, instruction.operandSlot) { operand ->
    operand.truncI32u()
}

internal inline fun I32TruncSatF64SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32TruncSatF64SI,
) = executeF64ToI32I(vstack, instruction.destinationSlot, instruction.operand) { operand ->
    operand.truncI32s()
}

internal inline fun I32TruncSatF64SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32TruncSatF64SS,
) = executeF64ToI32S(vstack, instruction.destinationSlot, instruction.operandSlot) { operand ->
    operand.truncI32s()
}

internal inline fun I32TruncSatF64UExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32TruncSatF64UI,
) = executeF64ToI32I(vstack, instruction.destinationSlot, instruction.operand) { operand ->
    operand.truncI32u()
}

internal inline fun I32TruncSatF64UExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32TruncSatF64US,
) = executeF64ToI32S(vstack, instruction.destinationSlot, instruction.operandSlot) { operand ->
    operand.truncI32u()
}

internal inline fun I32WrapI64Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32WrapI64I,
) = executeI64ToI32I(vstack, instruction.destinationSlot, instruction.operand) { operand ->
    operand.wrap()
}

internal inline fun I32WrapI64Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I32WrapI64S,
) = executeI64ToI32S(vstack, instruction.destinationSlot, instruction.operandSlot) { operand ->
    operand.wrap()
}
