package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.binaryOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun I32DivSExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32DivS,
) {
    val stack = context.vstack
    val operand1 = stack.peekNthI32(1)
    val operand2 = stack.peekNthI32(0)

    if (operand1 == Int.MIN_VALUE && operand2 == -1) {
        throw InvocationException(InvocationError.IntegerOverflow)
    }

    try {
        stack.binaryOperation(Int::div)
    } catch (_: ArithmeticException) {
        throw InvocationException(InvocationError.CannotDivideIntegerByZero)
    }
}
