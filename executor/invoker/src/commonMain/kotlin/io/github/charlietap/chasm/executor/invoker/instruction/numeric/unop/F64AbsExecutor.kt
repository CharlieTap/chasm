@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.unaryOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction
import kotlin.math.absoluteValue

internal inline fun F64AbsExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.F64Abs,
): Result<Unit, InvocationError> {
    return context.stack.unaryOperation(Double::absoluteValue)
}
