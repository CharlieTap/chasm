package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.ext.array
import io.github.charlietap.chasm.runtime.ext.popArrayAddress
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun ArrayCopyExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayCopy,
): InstructionPointer {
    // x = dest
    // y = src

    val elementsToCopy = vstack.popI32()

    val sourceOffset = vstack.popI32()
    val sourceAddress = vstack.popArrayAddress()
    val source = store.array(sourceAddress)

    val destinationOffset = vstack.popI32()
    val destinationAddress = vstack.popArrayAddress()
    val destination = store.array(destinationAddress)

    try {
        source.fields.copyInto(destination.fields, destinationOffset, sourceOffset, sourceOffset + elementsToCopy)
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }

    return ip + 1
}
