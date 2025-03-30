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

internal inline fun ArrayInitElementExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayInitElement,
): InstructionPointer {
    val elementInstance = instruction.elementInstance

    val elementsToCopy = vstack.popI32()
    val sourceOffsetInElementSegment = vstack.popI32()
    val destinationOffsetInArray = vstack.popI32()
    val address = vstack.popArrayAddress()
    val arrayInstance = store.array(address)

    try {
        elementInstance.elements.copyInto(arrayInstance.fields, destinationOffsetInArray, sourceOffsetInElementSegment, sourceOffsetInElementSegment + elementsToCopy)
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.TableOperationOutOfBounds)
    }

    return ip + 1
}
