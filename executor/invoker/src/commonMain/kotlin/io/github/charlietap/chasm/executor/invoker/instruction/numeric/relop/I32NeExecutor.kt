@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.invoker.ext.ne
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.relationalOperation

internal inline fun I32NeExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32Ne,
): Result<Unit, InvocationError> {
    return context.stack.relationalOperation(Int::ne)
}
