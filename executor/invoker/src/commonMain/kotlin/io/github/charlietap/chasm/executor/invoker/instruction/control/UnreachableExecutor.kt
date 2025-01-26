package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

internal inline fun UnreachableExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.Unreachable,
) {
    throw InvocationException(InvocationError.Unreachable)
}
