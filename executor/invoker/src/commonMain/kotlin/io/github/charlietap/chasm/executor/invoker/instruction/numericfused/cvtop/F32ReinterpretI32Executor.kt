package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun F32ReinterpretI32Executor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32ReinterpretI32,
) {
    val stack = context.vstack

    val operand = instruction.operand(stack).toInt()
    val result = try {
        Float.fromBits(operand).toRawBits().toLong()
    } catch (_: Throwable) {
        throw InvocationException(InvocationError.ConvertOperationFailed)
    }

    instruction.destination(result, stack)
}
