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

    if (left == Long.MIN_VALUE && right == -1L) {
        throw InvocationException(InvocationError.IntegerOverflow)
    }

    try {
        instruction.destination(left / right, stack)
    } catch (_: ArithmeticException) {
        throw InvocationException(InvocationError.CannotDivideIntegerByZero)
    }
}
