package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun ArrayCopyExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayCopy,
) {
    // x = dest
    // y = src

    val elementsToCopy = vstack.popI32()

    val sourceOffset = vstack.popI32()
    val sourceReference = vstack.pop()

    val destinationOffset = vstack.popI32()
    val destinationReference = vstack.pop()

    try {
        context.heap.copyArray(
            sourceReference,
            sourceOffset,
            destinationReference,
            destinationOffset,
            elementsToCopy,
        )
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }
}
