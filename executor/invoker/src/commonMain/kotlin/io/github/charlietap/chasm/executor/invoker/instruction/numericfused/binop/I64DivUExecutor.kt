package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun I64DivUExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64DivU,
) {
    val stack = context.vstack

    val left = instruction.left(stack).toULong()
    val right = instruction.right(stack).toULong()

    try {
        instruction.destination((left / right).toLong(), stack)
    } catch (_: ArithmeticException) {
        throw InvocationException(InvocationError.CannotDivideIntegerByZero)
    }
}
