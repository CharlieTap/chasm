package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction

internal inline fun I64DivSExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64DivS,
) {
    val stack = context.vstack

    val left = instruction.left(stack)
    val right = instruction.right(stack)

    if (right == 0L) {
        throw InvocationException(InvocationError.CannotDivideIntegerByZero)
    } else if (left == Long.MIN_VALUE && right == -1L) {
        throw InvocationException(InvocationError.IntegerOverflow)
    }

    instruction.destination(left / right, stack)
}
