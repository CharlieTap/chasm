package io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.ext.array
import io.github.charlietap.chasm.runtime.ext.toArrayAddress
import io.github.charlietap.chasm.runtime.instruction.FusedAggregateInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun ArrayCopyExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedAggregateInstruction.ArrayCopy,
): InstructionPointer {
    // x = dest
    // y = src

    val elementsToCopy = instruction.elementsToCopy(vstack).toInt()

    val sourceOffset = instruction.sourceOffset(vstack).toInt()
    val sourceAddress = instruction.sourceAddress(vstack).toArrayAddress()
    val source = store.array(sourceAddress)

    val destinationOffset = instruction.destinationOffset(vstack).toInt()
    val destinationAddress = instruction.destinationAddress(vstack).toArrayAddress()
    val destination = store.array(destinationAddress)

    try {
        source.fields.copyInto(destination.fields, destinationOffset, sourceOffset, sourceOffset + elementsToCopy)
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }

    return ip + 1
}
