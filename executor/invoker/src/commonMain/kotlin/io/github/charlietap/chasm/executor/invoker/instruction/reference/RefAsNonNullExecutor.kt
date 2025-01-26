package io.github.charlietap.chasm.executor.invoker.instruction.reference

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.peekReference
import io.github.charlietap.chasm.executor.runtime.instruction.ReferenceInstruction
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal inline fun RefAsNonNullExecutor(
    context: ExecutionContext,
    instruction: ReferenceInstruction.RefAsNonNull,
) {
    val stack = context.vstack
    val value = stack.peekReference()

    if (value is ReferenceValue.Null) {
        throw InvocationException(InvocationError.Trap.TrapEncountered)
    }
}
