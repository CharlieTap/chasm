package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import io.github.charlietap.chasm.executor.invoker.ext.remu
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.binaryOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun I64RemUExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I64RemU,
) {
    val operand = context.vstack.peekNthI64(0)

    if (operand.toULong() == 0uL) {
        throw InvocationException(InvocationError.CannotDivideIntegerByZero)
    }

    context.vstack.binaryOperation(Long::remu)
}
