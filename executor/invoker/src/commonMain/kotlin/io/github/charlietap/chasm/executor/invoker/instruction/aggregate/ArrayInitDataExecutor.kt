package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.invoker.ext.valueFromBytes
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.array
import io.github.charlietap.chasm.executor.runtime.ext.popArrayReference
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction

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
    val arrayRef = stack.popArrayReference()
    val arrayInstance = store.array(arrayRef.address)
    val arrayType = arrayInstance.arrayType
    val fieldWidthInBytes = instruction.fieldWidthInBytes

    if (
        (arrayOffset + elementsToCopy > arrayInstance.fields.size) ||
        (byteArrayOffset + (elementsToCopy * fieldWidthInBytes) > dataInstance.bytes.size)
    ) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }

    val byteArray = dataInstance.bytes
    repeat(elementsToCopy) { offset ->

        val srcOffsetInByteArray = byteArrayOffset + (fieldWidthInBytes * offset)
        val element = arrayType.fieldType.valueFromBytes(byteArray, srcOffsetInByteArray)

        val fieldIndex = arrayOffset + offset

        arrayInstance.fields[fieldIndex] = element
    }
}
