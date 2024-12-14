package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

internal inline fun NopExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.Nop,
): Result<Unit, InvocationError> = Ok(Unit)
