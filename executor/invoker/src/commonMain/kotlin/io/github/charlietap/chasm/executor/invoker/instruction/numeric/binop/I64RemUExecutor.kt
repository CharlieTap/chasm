package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import io.github.charlietap.chasm.executor.invoker.ext.bind
import io.github.charlietap.chasm.executor.invoker.ext.remu
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.binaryOperation
import io.github.charlietap.chasm.executor.runtime.ext.peekNthValue
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.I64

internal inline fun I64RemUExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I64RemU,
) {
    val operand2 = context.stack
        .peekNthValue(0).bind()
         as I64

    if (operand2.value.toULong() == 0uL) {
        throw InvocationException(InvocationError.CannotDivideIntegerByZero)
    }

    context.stack.binaryOperation(Long::remu)
}
