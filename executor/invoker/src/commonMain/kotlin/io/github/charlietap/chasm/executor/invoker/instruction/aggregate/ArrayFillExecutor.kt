package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun ArrayFillExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayFill,
) {
    val elementsToFill = vstack.popI32()
    val fillValue = vstack.pop()
    val arrayElementOffset = vstack.popI32()
    val reference = vstack.pop()

    try {
        context.heap.fillArray(reference, arrayElementOffset, elementsToFill, fillValue)
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }
}
