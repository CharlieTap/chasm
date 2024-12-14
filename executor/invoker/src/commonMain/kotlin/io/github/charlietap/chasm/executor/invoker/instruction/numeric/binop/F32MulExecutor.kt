package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.binaryOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

internal inline fun F32MulExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.F32Mul,
): Result<Unit, InvocationError> {
    return context.stack.binaryOperation(Float::times)
}
