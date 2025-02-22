package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction

internal inline fun I64DivUExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64DivU,
) {
    val stack = context.vstack

    val left = instruction.left(stack).toULong()
    val right = instruction.right(stack).toULong()

    if (right == 0UL) {
        throw InvocationException(InvocationError.CannotDivideIntegerByZero)
    }

    instruction.destination((left / right).toLong(), stack)
}
