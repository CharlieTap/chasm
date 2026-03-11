package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.truncI64s
import io.github.charlietap.chasm.executor.invoker.ext.truncI64sTrapping
import io.github.charlietap.chasm.executor.invoker.ext.truncI64u
import io.github.charlietap.chasm.executor.invoker.ext.truncI64uTrapping
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.NumericSuperInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun I64ReinterpretF64Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64ReinterpretF64I,
) = executeF64ToI64I(vstack, instruction.destinationSlot, instruction.operand) { operand ->
    operand.toRawBits()
}

internal inline fun I64ReinterpretF64Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64ReinterpretF64S,
) = executeF64ToI64S(vstack, instruction.destinationSlot, instruction.operandSlot) { operand ->
    operand.toRawBits()
}

internal inline fun I64TruncF32SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64TruncF32SI,
) = executeF32ToI64I(vstack, instruction.destinationSlot, instruction.operand) { operand ->
    try {
        operand.truncI64sTrapping()
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.ConvertOperationFailed)
    }
}

internal inline fun I64TruncF32SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64TruncF32SS,
) = executeF32ToI64S(vstack, instruction.destinationSlot, instruction.operandSlot) { operand ->
    try {
        operand.truncI64sTrapping()
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.ConvertOperationFailed)
    }
}

internal inline fun I64TruncF32UExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64TruncF32UI,
) = executeF32ToI64I(vstack, instruction.destinationSlot, instruction.operand) { operand ->
    try {
        operand.truncI64uTrapping()
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.ConvertOperationFailed)
    }
}

internal inline fun I64TruncF32UExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64TruncF32US,
) = executeF32ToI64S(vstack, instruction.destinationSlot, instruction.operandSlot) { operand ->
    try {
        operand.truncI64uTrapping()
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.ConvertOperationFailed)
    }
}

internal inline fun I64TruncF64SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64TruncF64SI,
) = executeF64ToI64I(vstack, instruction.destinationSlot, instruction.operand) { operand ->
    try {
        operand.truncI64sTrapping()
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.ConvertOperationFailed)
    }
}

internal inline fun I64TruncF64SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64TruncF64SS,
) = executeF64ToI64S(vstack, instruction.destinationSlot, instruction.operandSlot) { operand ->
    try {
        operand.truncI64sTrapping()
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.ConvertOperationFailed)
    }
}

internal inline fun I64TruncF64UExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64TruncF64UI,
) = executeF64ToI64I(vstack, instruction.destinationSlot, instruction.operand) { operand ->
    try {
        operand.truncI64uTrapping()
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.ConvertOperationFailed)
    }
}

internal inline fun I64TruncF64UExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64TruncF64US,
) = executeF64ToI64S(vstack, instruction.destinationSlot, instruction.operandSlot) { operand ->
    try {
        operand.truncI64uTrapping()
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.ConvertOperationFailed)
    }
}

internal inline fun I64TruncSatF32SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64TruncSatF32SI,
) = executeF32ToI64I(vstack, instruction.destinationSlot, instruction.operand) { operand ->
    operand.truncI64s()
}

internal inline fun I64TruncSatF32SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64TruncSatF32SS,
) = executeF32ToI64S(vstack, instruction.destinationSlot, instruction.operandSlot) { operand ->
    operand.truncI64s()
}

internal inline fun I64TruncSatF32UExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64TruncSatF32UI,
) = executeF32ToI64I(vstack, instruction.destinationSlot, instruction.operand) { operand ->
    operand.truncI64u()
}

internal inline fun I64TruncSatF32UExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64TruncSatF32US,
) = executeF32ToI64S(vstack, instruction.destinationSlot, instruction.operandSlot) { operand ->
    operand.truncI64u()
}

internal inline fun I64TruncSatF64SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64TruncSatF64SI,
) = executeF64ToI64I(vstack, instruction.destinationSlot, instruction.operand) { operand ->
    operand.truncI64s()
}

internal inline fun I64TruncSatF64SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64TruncSatF64SS,
) = executeF64ToI64S(vstack, instruction.destinationSlot, instruction.operandSlot) { operand ->
    operand.truncI64s()
}

internal inline fun I64TruncSatF64UExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64TruncSatF64UI,
) = executeF64ToI64I(vstack, instruction.destinationSlot, instruction.operand) { operand ->
    operand.truncI64u()
}

internal inline fun I64TruncSatF64UExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericSuperInstruction.I64TruncSatF64US,
) = executeF64ToI64S(vstack, instruction.destinationSlot, instruction.operandSlot) { operand ->
    operand.truncI64u()
}
