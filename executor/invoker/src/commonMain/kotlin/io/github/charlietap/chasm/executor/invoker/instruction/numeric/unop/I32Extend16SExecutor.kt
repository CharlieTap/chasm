@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.invoker.ext.extend16s
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.unaryOperation

internal inline fun I32Extend16SExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32Extend16S,
): Result<Unit, InvocationError> {
    return context.stack.unaryOperation(Int::extend16s)
}
