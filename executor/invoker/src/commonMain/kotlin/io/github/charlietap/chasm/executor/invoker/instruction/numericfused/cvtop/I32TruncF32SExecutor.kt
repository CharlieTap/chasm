package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.truncI32sTrapping
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun I32TruncF32SExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I32TruncF32S,
) {
    val stack = context.vstack

    val operand = Float.fromBits(instruction.operand(stack).toInt())
    val result = try {
        operand.truncI32sTrapping().toLong()
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.ConvertOperationFailed)
    }

    instruction.destination(result, stack)
}
