package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.array
import io.github.charlietap.chasm.executor.runtime.ext.popArrayAddress
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction

internal inline fun ArraySetExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArraySet,
) {
    val store = context.store
    val stack = context.vstack

    val value = stack.pop()
    val fieldIndex = stack.popI32()
    val address = stack.popArrayAddress()

    val arrayInstance = store.array(address)

    try {
        arrayInstance.fields[fieldIndex] = value
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }
}
