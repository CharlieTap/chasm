package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction

internal inline fun I32DivSExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I32DivS,
) {
    val stack = context.vstack

    val left = instruction.left(stack).toInt()
    val right = instruction.right(stack).toInt()

    if (right == 0) {
        throw InvocationException(InvocationError.CannotDivideIntegerByZero)
    } else if (left == Int.MIN_VALUE && right == -1) {
        throw InvocationException(InvocationError.IntegerOverflow)
    }

    instruction.destination((left / right).toLong(), stack)
}
