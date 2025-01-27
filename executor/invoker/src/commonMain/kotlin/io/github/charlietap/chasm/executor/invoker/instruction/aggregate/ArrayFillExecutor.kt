package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.array
import io.github.charlietap.chasm.executor.runtime.ext.popArrayReference
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction

internal fun ArrayFillExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayFill,
) = ArrayFillExecutor(
    context = context,
    instruction = instruction,
    fieldPacker = ::FieldPacker,
)

internal inline fun ArrayFillExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayFill,
    crossinline fieldPacker: FieldPacker,
) {
    val store = context.store
    val stack = context.vstack

    val elementsToFill = stack.popI32()
    val fillValue = stack.pop()
    val arrayElementOffset = stack.popI32()
    val arrayRef = stack.popArrayReference()
    val arrayInstance = store.array(arrayRef.address)

    if (arrayElementOffset + elementsToFill > arrayInstance.fields.size) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }

    repeat(elementsToFill) { fillOffset ->

        val fieldIndex = arrayElementOffset + fillOffset
        val fieldValue = fieldPacker(fillValue, arrayInstance.arrayType.fieldType)

        arrayInstance.fields[fieldIndex] = fieldValue
    }
}
