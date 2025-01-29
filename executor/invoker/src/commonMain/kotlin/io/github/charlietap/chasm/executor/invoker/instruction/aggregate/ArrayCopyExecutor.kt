package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.array
import io.github.charlietap.chasm.executor.runtime.ext.popArrayAddress
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction

internal inline fun ArrayCopyExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayCopy,
) {
    // x = dest
    // y = src
    val store = context.store
    val stack = context.vstack

    val elementsToCopy = stack.popI32()

    val sourceOffset = stack.popI32()
    val sourceAddress = stack.popArrayAddress()
    val source = store.array(sourceAddress)

    val destinationOffset = stack.popI32()
    val destinationAddress = stack.popArrayAddress()
    val destination = store.array(destinationAddress)

    try {
        source.fields.copyInto(destination.fields, destinationOffset, sourceOffset, sourceOffset + elementsToCopy)
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }
}
