package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.truncI32sTrapping
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun I32TruncF64SExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I32TruncF64S,
) {
    val stack = context.vstack

    val operand = Double.fromBits(instruction.operand(stack))
    val result = try {
        operand.truncI32sTrapping().toLong()
    } catch (e: IllegalArgumentException) {
        throw InvocationException(InvocationError.ConvertOperationFailed)
    }

    instruction.destination(result, stack)
}
