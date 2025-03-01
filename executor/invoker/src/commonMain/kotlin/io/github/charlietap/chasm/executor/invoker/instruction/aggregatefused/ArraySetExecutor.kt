package io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.array
import io.github.charlietap.chasm.executor.runtime.ext.toArrayAddress
import io.github.charlietap.chasm.executor.runtime.instruction.FusedAggregateInstruction

internal inline fun ArraySetExecutor(
    context: ExecutionContext,
    instruction: FusedAggregateInstruction.ArraySet,
) {
    val store = context.store
    val stack = context.vstack

    val value = instruction.value(stack)
    val fieldIndex = instruction.field(stack).toInt()
    val address = instruction.address(stack).toArrayAddress()

    val arrayInstance = store.array(address)

    try {
        arrayInstance.fields[fieldIndex] = value
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }
}
