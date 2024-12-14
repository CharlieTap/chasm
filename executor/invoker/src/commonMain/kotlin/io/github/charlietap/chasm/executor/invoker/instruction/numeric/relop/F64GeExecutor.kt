package io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.invoker.ext.ge
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.relationalOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

internal inline fun F64GeExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.F64Ge,
): Result<Unit, InvocationError> {
    return context.stack.relationalOperation(Double::ge)
}
