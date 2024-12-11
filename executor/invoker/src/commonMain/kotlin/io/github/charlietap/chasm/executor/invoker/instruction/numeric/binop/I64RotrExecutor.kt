@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.invoker.ext.rotateRight
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.binaryOperation

internal inline fun I64RotrExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I64Rotr,
): Result<Unit, InvocationError> {
    return context.stack.binaryOperation(Long::rotateRight)
}
