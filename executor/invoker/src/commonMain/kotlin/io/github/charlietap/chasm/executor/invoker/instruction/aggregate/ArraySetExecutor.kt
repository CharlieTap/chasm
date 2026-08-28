package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun ArraySetExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: AggregateInstruction.ArraySet,
) {
    val value = vstack.pop()
    val fieldIndex = vstack.popI32()
    val reference = vstack.pop()

    try {
        context.heap.setArrayElementTrusted(reference, fieldIndex, value)
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }
}
