package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.invoker.ext.valueFromBytes
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.toLong
import io.github.charlietap.chasm.executor.runtime.instance.ArrayInstance
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal inline fun ArrayNewDataExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayNewData,
) {
    val stack = context.vstack
    val store = context.store

    val arrayType = instruction.arrayType
    val dataInstance = instruction.dataInstance
    val byteArray = dataInstance.bytes
    val fieldWidthInBytes = instruction.fieldWidthInBytes

    val arrayLength = stack.popI32()
    val arrayStartOffsetInSegment = stack.popI32()

    val arrayEndOffsetInSegment = arrayStartOffsetInSegment + (arrayLength * fieldWidthInBytes)
    if (arrayEndOffsetInSegment > byteArray.size) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }

    val fields = LongArray(arrayLength) { offset ->
        val elementOffset = arrayStartOffsetInSegment + (offset * fieldWidthInBytes)
        arrayType.fieldType.valueFromBytes(byteArray, elementOffset)
    }

    val instance = ArrayInstance(instruction.definedType, instruction.arrayType, fields)
    store.arrays.add(instance)
    val reference = ReferenceValue.Array(Address.Array(store.arrays.size - 1))

    stack.push(reference.toLong())
}
