package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.array
import io.github.charlietap.chasm.executor.runtime.ext.popArrayReference
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction

internal inline fun ArrayInitElementExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayInitElement,
) {
    val stack = context.vstack
    val store = context.store
    val elementInstance = instruction.elementInstance

    val elementsToCopy = stack.popI32()
    val sourceOffsetInElementSegment = stack.popI32()
    val destinationOffsetInArray = stack.popI32()
    val arrayRef = stack.popArrayReference()
    val arrayInstance = store.array(arrayRef.address)

    if (
        (destinationOffsetInArray + elementsToCopy > arrayInstance.fields.size) ||
        (sourceOffsetInElementSegment + elementsToCopy > elementInstance.elements.size)
    ) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }

    repeat(elementsToCopy) { offset ->

        val elementIndex = sourceOffsetInElementSegment + offset
        val elementValue = elementInstance.elements[elementIndex]
        val fieldIndex = destinationOffsetInArray + offset

        arrayInstance.fields[fieldIndex] = elementValue
    }
}
