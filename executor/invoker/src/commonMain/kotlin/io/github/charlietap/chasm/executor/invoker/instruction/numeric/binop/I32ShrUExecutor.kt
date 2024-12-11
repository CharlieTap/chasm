@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.invoker.ext.shru
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.binaryOperation

internal inline fun I32ShrUExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32ShrU,
): Result<Unit, InvocationError> {
    return context.stack.binaryOperation(Int::shru)
}
