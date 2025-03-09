package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun I32RemSExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I32RemS,
) {
    val stack = context.vstack

    val right = instruction.right(stack).toInt()

    if (right == 0) {
        throw InvocationException(InvocationError.CannotDivideIntegerByZero)
    }

    val left = instruction.left(stack).toInt()
    val result = left.rem(right)

    instruction.destination(result.toLong(), stack)
}
