package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.invoker.ext.valueFromBytes
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.array
import io.github.charlietap.chasm.executor.runtime.ext.popArrayReference
import io.github.charlietap.chasm.executor.runtime.ext.toLong
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.type.ext.bitWidth

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

    val arrayElementSizeInBytes = arrayType.fieldType.bitWidth()?.let {
        it / 8
    } ?: throw InvocationException(InvocationError.UnobservableBitWidth)

    if (
        (arrayOffset + elementsToCopy > arrayInstance.fields.size) ||
        (byteArrayOffset + (elementsToCopy * arrayElementSizeInBytes) > dataInstance.bytes.size)
    ) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }

    repeat(elementsToCopy) { offset ->

        val srcOffsetInByteArray = byteArrayOffset + (arrayElementSizeInBytes * offset)
        val endOffsetInByteArray = srcOffsetInByteArray + arrayElementSizeInBytes

        val byteArray = dataInstance.bytes.sliceArray(srcOffsetInByteArray until endOffsetInByteArray)
        val element = arrayType.fieldType.valueFromBytes(byteArray)

        val fieldIndex = arrayOffset + offset
        val fieldValue = element.toLong()

        arrayInstance.fields[fieldIndex] = fieldValue
    }
}
