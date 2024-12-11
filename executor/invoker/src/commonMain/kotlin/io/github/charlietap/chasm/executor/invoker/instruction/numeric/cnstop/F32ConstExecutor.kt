@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cnstop

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.constOperation

internal inline fun F32ConstExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.F32Const,
): Result<Unit, InvocationError> {
    return Ok(context.stack.constOperation(instruction.value))
}
