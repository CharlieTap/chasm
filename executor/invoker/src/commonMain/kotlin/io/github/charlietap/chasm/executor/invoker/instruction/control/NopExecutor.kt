package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.error.InvocationError

internal inline fun NopExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.Nop,
): Result<Unit, InvocationError> = Ok(Unit)
