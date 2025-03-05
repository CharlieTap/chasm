package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.array
import io.github.charlietap.chasm.runtime.ext.popArrayAddress
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

internal inline fun ArrayFillExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayFill,
) {
    val store = context.store
    val stack = context.vstack

    val elementsToFill = stack.popI32()
    val fillValue = stack.pop()
    val arrayElementOffset = stack.popI32()
    val address = stack.popArrayAddress()
    val arrayInstance = store.array(address)

    try {
        arrayInstance.fields.fill(fillValue, arrayElementOffset, arrayElementOffset + elementsToFill)
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }
}
