@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.invoker.ext.floor
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.unaryOperation

internal inline fun F64FloorExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.F64Floor,
): Result<Unit, InvocationError> {
    return context.stack.unaryOperation(Double::floor)
}
