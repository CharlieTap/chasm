package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun ArrayInitElementExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayInitElement,
) {
    val elementInstance = instruction.elementInstance

    val elementsToCopy = vstack.popI32()
    val sourceOffsetInElementSegment = vstack.popI32()
    val destinationOffsetInArray = vstack.popI32()
    val reference = vstack.pop()

    try {
        context.heap.initializeArrayFromElements(
            reference,
            destinationOffsetInArray,
            elementInstance.elements,
            sourceOffsetInElementSegment,
            elementsToCopy,
        )
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }
}
