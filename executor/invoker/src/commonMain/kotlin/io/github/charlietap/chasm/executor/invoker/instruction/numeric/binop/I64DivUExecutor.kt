package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import io.github.charlietap.chasm.executor.invoker.ext.divu
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.binaryOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun I64DivUExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I64DivU,
) {
    try {
        context.vstack.binaryOperation(Long::divu)
    } catch (_: ArithmeticException) {
        throw InvocationException(InvocationError.CannotDivideIntegerByZero)
    }
}
