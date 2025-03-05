package io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.array
import io.github.charlietap.chasm.runtime.ext.toArrayAddress
import io.github.charlietap.chasm.runtime.instruction.FusedAggregateInstruction

internal inline fun ArrayCopyExecutor(
    context: ExecutionContext,
    instruction: FusedAggregateInstruction.ArrayCopy,
) {
    // x = dest
    // y = src
    val store = context.store
    val stack = context.vstack

    val elementsToCopy = instruction.elementsToCopy(stack).toInt()

    val sourceOffset = instruction.sourceOffset(stack).toInt()
    val sourceAddress = instruction.sourceAddress(stack).toArrayAddress()
    val source = store.array(sourceAddress)

    val destinationOffset = instruction.destinationOffset(stack).toInt()
    val destinationAddress = instruction.destinationAddress(stack).toArrayAddress()
    val destination = store.array(destinationAddress)

    try {
        source.fields.copyInto(destination.fields, destinationOffset, sourceOffset, sourceOffset + elementsToCopy)
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }
}
