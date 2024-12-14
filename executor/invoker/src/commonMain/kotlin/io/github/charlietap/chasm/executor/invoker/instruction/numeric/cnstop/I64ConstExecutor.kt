package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cnstop

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.constOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

internal inline fun I64ConstExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I64Const,
): Result<Unit, InvocationError> {
    return Ok(context.stack.constOperation(instruction.value))
}
