package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun I32DivSExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I32DivS,
) {
    val stack = context.vstack

    val left = instruction.left(stack).toInt()
    val right = instruction.right(stack).toInt()

    if (left == Int.MIN_VALUE && right == -1) {
        throw InvocationException(InvocationError.IntegerOverflow)
    }

    try {
        instruction.destination((left / right).toLong(), stack)
    } catch (_: ArithmeticException) {
        throw InvocationException(InvocationError.CannotDivideIntegerByZero)
    }
}
