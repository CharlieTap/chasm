package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.instance.ArrayInstance
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.runtime.value.ReferenceValue

internal inline fun ArrayNewElementExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayNewElement,
) {
    val stack = context.vstack
    val store = context.store

    val elementInstance = instruction.elementInstance
    val elements = elementInstance.elements

    val arrayLength = stack.popI32()
    val arrayStartOffsetInSegment = stack.popI32()
    val arrayEndOffsetInSegment = arrayStartOffsetInSegment + arrayLength

    if (arrayEndOffsetInSegment > elementInstance.elements.size) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }

    val fields = LongArray(arrayLength)
    elements.copyInto(fields, 0, arrayStartOffsetInSegment, arrayEndOffsetInSegment)

    val instance = ArrayInstance(instruction.definedType, instruction.arrayType, fields)
    store.arrays.add(instance)
    val reference = ReferenceValue.Array(Address.Array(store.arrays.size - 1))

    stack.push(reference.toLong())
}
