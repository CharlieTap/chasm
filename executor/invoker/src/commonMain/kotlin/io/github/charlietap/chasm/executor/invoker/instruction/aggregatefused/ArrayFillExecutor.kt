package io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.array
import io.github.charlietap.chasm.runtime.ext.toArrayAddress
import io.github.charlietap.chasm.runtime.instruction.FusedAggregateInstruction

internal inline fun ArrayFillExecutor(
    context: ExecutionContext,
    instruction: FusedAggregateInstruction.ArrayFill,
) {
    val store = context.store
    val stack = context.vstack

    val elementsToFill = instruction.elementsToFill(stack).toInt()
    val fillValue = instruction.fillValue(stack)
    val arrayElementOffset = instruction.arrayElementOffset(stack).toInt()
    val address = instruction.address(stack).toArrayAddress()
    val arrayInstance = store.array(address)

    try {
        arrayInstance.fields.fill(fillValue, arrayElementOffset, arrayElementOffset + elementsToFill)
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }
}
