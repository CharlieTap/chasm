package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.truncI32sTrapping
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun I32TruncF32SExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I32TruncF32S,
): InstructionPointer {
    val operand = Float.fromBits(instruction.operand(vstack).toInt())
    val result = try {
        operand.truncI32sTrapping().toLong()
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.ConvertOperationFailed)
    }

    instruction.destination(result, vstack)

    return ip + 1
}
