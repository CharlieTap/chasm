package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.binaryOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun I64DivSExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I64DivS,
) {
    val stack = context.vstack
    val operand1 = stack.peekNthI64(1)
    val operand2 = stack.peekNthI64(0)

    if (operand1 == Long.MIN_VALUE && operand2 == -1L) {
        throw InvocationException(InvocationError.IntegerOverflow)
    }

    try {
        stack.binaryOperation(Long::div)
    } catch (_: ArithmeticException) {
        throw InvocationException(InvocationError.CannotDivideIntegerByZero)
    }
}
