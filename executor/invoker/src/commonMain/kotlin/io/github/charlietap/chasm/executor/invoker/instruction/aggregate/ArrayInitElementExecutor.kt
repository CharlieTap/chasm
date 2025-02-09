package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.array
import io.github.charlietap.chasm.executor.runtime.ext.popArrayAddress
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
    val address = stack.popArrayAddress()
    val arrayInstance = store.array(address)

    try {
        elementInstance.elements.copyInto(arrayInstance.fields, destinationOffsetInArray, sourceOffsetInElementSegment, sourceOffsetInElementSegment + elementsToCopy)
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.TableOperationOutOfBounds)
    }
}
