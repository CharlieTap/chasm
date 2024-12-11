@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.binaryOperation

internal inline fun F64AddExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.F64Add,
): Result<Unit, InvocationError> {
    return context.stack.binaryOperation(Double::plus)
}
