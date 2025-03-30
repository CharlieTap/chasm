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

internal inline fun ArrayFillExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedAggregateInstruction.ArrayFill,
): InstructionPointer {
    val elementsToFill = instruction.elementsToFill(vstack).toInt()
    val fillValue = instruction.fillValue(vstack)
    val arrayElementOffset = instruction.arrayElementOffset(vstack).toInt()
    val address = instruction.address(vstack).toArrayAddress()
    val arrayInstance = store.array(address)

    try {
        arrayInstance.fields.fill(fillValue, arrayElementOffset, arrayElementOffset + elementsToFill)
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }

    return ip + 1
}
