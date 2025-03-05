package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.invoker.ext.valueFromBytes
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.array
import io.github.charlietap.chasm.runtime.ext.popArrayAddress
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

internal inline fun ArrayInitDataExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayInitData,
) {
    val stack = context.vstack
    val store = context.store
    val dataInstance = instruction.dataInstance

    val elementsToCopy = stack.popI32()
    val byteArrayOffset = stack.popI32()
    val arrayOffset = stack.popI32()
    val address = stack.popArrayAddress()
    val arrayInstance = store.array(address)
    val arrayType = arrayInstance.arrayType
    val fieldWidthInBytes = instruction.fieldWidthInBytes

    if (elementsToCopy == 0) {
        // Spec requires us to check bounds even if initialising zero elements
        if (
            (arrayOffset + elementsToCopy > arrayInstance.fields.size) ||
            (byteArrayOffset + (elementsToCopy * fieldWidthInBytes) > dataInstance.bytes.size)
        ) {
            throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
        }
        return
    }

    val byteArray = dataInstance.bytes
    try {
        val elements = LongArray(elementsToCopy) { offset ->
            val srcOffsetInByteArray = byteArrayOffset + (fieldWidthInBytes * offset)
            arrayType.fieldType.valueFromBytes(byteArray, srcOffsetInByteArray)
        }
        elements.copyInto(arrayInstance.fields, arrayOffset)
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }
}
