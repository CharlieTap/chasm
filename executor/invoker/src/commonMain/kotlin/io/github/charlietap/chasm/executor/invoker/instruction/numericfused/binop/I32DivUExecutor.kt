package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction

internal inline fun I32DivUExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I32DivU,
) {
    val stack = context.vstack

    val left = instruction.left(stack).toUInt()
    val right = instruction.right(stack).toUInt()

    if (right == 0U) {
        throw InvocationException(InvocationError.CannotDivideIntegerByZero)
    }

    instruction.destination((left / right).toLong(), stack)
}
