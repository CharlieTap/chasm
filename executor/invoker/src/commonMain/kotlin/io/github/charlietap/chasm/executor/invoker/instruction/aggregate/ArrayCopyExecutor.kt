package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.ast.type.Mutability
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.array
import io.github.charlietap.chasm.executor.runtime.ext.popArrayReference
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
    val sourceRef = stack.popArrayReference()
    val source = store.array(sourceRef.address)

    val destinationOffset = stack.popI32()
    val destinationRef = stack.popArrayReference()
    val destination = store.array(destinationRef.address)

    if (destination.arrayType.fieldType.mutability != Mutability.Var) {
        throw InvocationException(InvocationError.ArrayCopyOnAConstArray)
    }

    if (destinationOffset + elementsToCopy > destination.fields.size) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }

    if (sourceOffset + elementsToCopy > source.fields.size) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }

    if (destinationOffset <= sourceOffset) {
        // forward copy
        repeat(elementsToCopy) { copyOffset ->
            val field = source.fields[sourceOffset + copyOffset]
            destination.fields[destinationOffset + copyOffset] = field
        }
    } else {
        // backward copy
        repeat(elementsToCopy) { copyOffset ->
            val field = source.fields[sourceOffset + elementsToCopy - 1 - copyOffset]
            destination.fields[destinationOffset + elementsToCopy - 1 - copyOffset] = field
        }
    }
}
