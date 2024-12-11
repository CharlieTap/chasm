@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.numeric.testop

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.invoker.ext.eqz
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.testOperation

internal inline fun I64EqzExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I64Eqz,
): Result<Unit, InvocationError> {
    return context.stack.testOperation(Long::eqz)
}
