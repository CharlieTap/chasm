package io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.invoker.ext.ne
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.relationalOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

internal inline fun F32NeExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.F32Ne,
): Result<Unit, InvocationError> {
    return context.stack.relationalOperation(Float::ne)
}
