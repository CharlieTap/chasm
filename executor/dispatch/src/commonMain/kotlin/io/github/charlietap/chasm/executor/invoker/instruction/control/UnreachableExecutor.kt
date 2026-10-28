package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun UnreachableExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: ControlInstruction.Unreachable,
) {
    throw InvocationException(InvocationError.Unreachable)
}
