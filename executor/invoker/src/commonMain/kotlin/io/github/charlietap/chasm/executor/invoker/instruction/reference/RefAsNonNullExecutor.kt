package io.github.charlietap.chasm.executor.invoker.instruction.reference

import io.github.charlietap.chasm.executor.runtime.encoder.isNullableReference
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.ReferenceInstruction

internal inline fun RefAsNonNullExecutor(
    context: ExecutionContext,
    instruction: ReferenceInstruction.RefAsNonNull,
) {
    val stack = context.vstack
    val value = stack.peek()

    if (value.isNullableReference()) {
        throw InvocationException(InvocationError.NonNullReferenceExpected)
    }
}
