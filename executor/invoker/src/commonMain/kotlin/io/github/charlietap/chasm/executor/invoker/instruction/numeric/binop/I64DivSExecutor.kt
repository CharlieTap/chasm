package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import io.github.charlietap.chasm.executor.invoker.ext.bind
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.binaryOperation
import io.github.charlietap.chasm.executor.runtime.ext.peekNthValue
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.I64

internal inline fun I64DivSExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I64DivS,
) {
    val stack = context.stack
    val operand1 = stack.peekNthValue(1).bind() as I64
    val operand2 = stack.peekNthValue(0).bind() as I64

    if (operand2.value == 0L) {
        throw InvocationException(InvocationError.CannotDivideIntegerByZero)
    } else if (operand1.value == Long.MIN_VALUE && operand2.value == -1L) {
        throw InvocationException(InvocationError.IntegerOverflow)
    }

    stack.binaryOperation(Long::div)
}
