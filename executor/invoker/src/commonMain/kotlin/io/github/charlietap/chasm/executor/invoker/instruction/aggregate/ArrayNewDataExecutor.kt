package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun ArrayNewDataExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayNewData,
) {
    val dataInstance = instruction.dataInstance
    val byteArray = dataInstance.bytes
    val fieldWidthInBytes = instruction.fieldWidthInBytes

    val arrayLength = vstack.popI32()
    val arrayStartOffsetInSegment = vstack.popI32()

    val reference = try {
        context.heap.allocateArrayFromData(
            context,
            instruction.rtt,
            byteArray,
            arrayStartOffsetInSegment,
            arrayLength,
            fieldWidthInBytes,
        )
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }
    vstack.push(reference)
}
