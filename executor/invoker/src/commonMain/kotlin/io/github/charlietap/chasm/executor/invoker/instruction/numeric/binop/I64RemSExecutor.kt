package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import io.github.charlietap.chasm.executor.invoker.ext.bind
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.binaryOperation
import io.github.charlietap.chasm.executor.runtime.ext.peekNthValue
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.I64

internal inline fun I64RemSExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I64RemS,
) {
    val operand2 = context.stack
        .peekNthValue(0)
        .bind()
        as I64

    if (operand2.value == 0L) {
        throw InvocationException(InvocationError.CannotDivideIntegerByZero)
    }

    context.stack.binaryOperation(Long::rem)
}
