package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun F64ReinterpretI64Executor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64ReinterpretI64,
) {
    val stack = context.vstack

    val operand = instruction.operand(stack)
    val result = try {
        Double.fromBits(operand).toRawBits()
    } catch (_: Throwable) {
        throw InvocationException(InvocationError.ConvertOperationFailed)
    }

    instruction.destination(result, stack)
}
