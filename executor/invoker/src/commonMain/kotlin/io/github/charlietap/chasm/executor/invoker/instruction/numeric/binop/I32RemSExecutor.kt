package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.binaryOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.I32

internal inline fun I32RemSExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32RemS,
) {
    val operand2 = context.vstack
        .peekNth(0)
        as I32

    if (operand2.value == 0) {
        throw InvocationException(InvocationError.CannotDivideIntegerByZero)
    }

    context.vstack.binaryOperation(Int::rem)
}
