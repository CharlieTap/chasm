package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.invoker.ext.copySign
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.binaryOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

internal inline fun F32CopysignExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.F32Copysign,
): Result<Unit, InvocationError> {
    return context.stack.binaryOperation(Float::copySign)
}
