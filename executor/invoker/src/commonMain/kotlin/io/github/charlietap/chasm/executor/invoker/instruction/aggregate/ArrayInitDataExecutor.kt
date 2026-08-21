package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun ArrayInitDataExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayInitData,
) {
    val dataInstance = instruction.dataInstance

    val elementsToCopy = vstack.popI32()
    val byteArrayOffset = vstack.popI32()
    val arrayOffset = vstack.popI32()
    val reference = vstack.pop()
    val fieldWidthInBytes = instruction.fieldWidthInBytes
    try {
        context.heap.initializeArrayFromData(
            reference,
            arrayOffset,
            dataInstance.bytes,
            byteArrayOffset,
            elementsToCopy,
            fieldWidthInBytes,
        )
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }
}
