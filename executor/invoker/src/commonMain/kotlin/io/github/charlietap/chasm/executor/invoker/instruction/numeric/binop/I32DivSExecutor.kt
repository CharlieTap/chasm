package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.binaryOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.I32

internal inline fun I32DivSExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32DivS,
) {
    val stack = context.vstack
    val operand1 = stack.peekNth(1) as I32
    val operand2 = stack.peekNth(0) as I32

    if (operand2.value == 0) {
        throw InvocationException(InvocationError.CannotDivideIntegerByZero)
    } else if (operand1.value == Int.MIN_VALUE && operand2.value == -1) {
        throw InvocationException(InvocationError.IntegerOverflow)
    }

    stack.binaryOperation(Int::div)
}
