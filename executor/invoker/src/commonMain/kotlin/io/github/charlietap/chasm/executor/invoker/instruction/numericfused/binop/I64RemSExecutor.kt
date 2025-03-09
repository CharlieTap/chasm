package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun I64RemSExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64RemS,
) {
    val stack = context.vstack

    val right = instruction.right(stack)

    if (right == 0L) {
        throw InvocationException(InvocationError.CannotDivideIntegerByZero)
    }

    val left = instruction.left(stack)
    val result = left.rem(right)

    instruction.destination(result, stack)
}
