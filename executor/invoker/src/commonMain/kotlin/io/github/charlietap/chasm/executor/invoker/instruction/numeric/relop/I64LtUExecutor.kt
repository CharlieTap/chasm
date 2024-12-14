@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.invoker.ext.ltu
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.relationalOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

internal inline fun I64LtUExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I64LtU,
): Result<Unit, InvocationError> {
    return context.stack.relationalOperation(Long::ltu)
}
