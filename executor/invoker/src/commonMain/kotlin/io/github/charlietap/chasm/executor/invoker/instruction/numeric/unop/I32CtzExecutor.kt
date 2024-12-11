@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.unaryOperation

internal inline fun I32CtzExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32Ctz,
): Result<Unit, InvocationError> {
    return context.stack.unaryOperation(Int::countTrailingZeroBits)
}
