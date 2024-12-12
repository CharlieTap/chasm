package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

internal inline fun UnreachableExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.Unreachable,
): Result<Unit, InvocationError> = Err(InvocationError.Trap.TrapEncountered)
