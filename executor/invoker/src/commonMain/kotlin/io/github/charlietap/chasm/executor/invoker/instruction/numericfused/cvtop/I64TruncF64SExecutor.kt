package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.truncI64sTrapping
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun I64TruncF64SExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64TruncF64S,
): InstructionPointer {
    val operand = Double.fromBits(instruction.operand(vstack))
    val result = try {
        operand.truncI64sTrapping()
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.ConvertOperationFailed)
    }

    instruction.destination(result, vstack)
    return ip + 1
}
