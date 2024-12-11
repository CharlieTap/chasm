@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.invoker.ext.ceil
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.unaryOperation

internal inline fun F64CeilExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.F64Ceil,
): Result<Unit, InvocationError> {
    return context.stack.unaryOperation(Double::ceil)
}
