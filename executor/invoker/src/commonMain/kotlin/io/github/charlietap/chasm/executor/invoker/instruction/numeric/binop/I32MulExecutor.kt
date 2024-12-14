package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.binaryOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

internal inline fun I32MulExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32Mul,
): Result<Unit, InvocationError> {
    return context.stack.binaryOperation(Int::times)
}
