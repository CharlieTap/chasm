package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Err
import io.github.charlietap.chasm.executor.invoker.ext.bind
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

internal inline fun UnreachableExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.Unreachable,
) {
    Err(InvocationError.Trap.TrapEncountered).bind()
}
