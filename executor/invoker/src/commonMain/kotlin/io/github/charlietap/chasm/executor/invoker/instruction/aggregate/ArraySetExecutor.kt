package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.array
import io.github.charlietap.chasm.executor.runtime.ext.popArrayReference
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction

internal fun ArraySetExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArraySet,
) =
    ArraySetExecutor(
        context = context,
        fieldPacker = ::FieldPacker,
    )

internal inline fun ArraySetExecutor(
    context: ExecutionContext,
    crossinline fieldPacker: FieldPacker,
) {
    val store = context.store
    val stack = context.vstack

    val value = stack.pop()
    val fieldIndex = stack.popI32()
    val arrayRef = stack.popArrayReference()

    val arrayInstance = store.array(arrayRef.address)
    val fieldValue = fieldPacker(value, arrayInstance.arrayType.fieldType)

    try {
        arrayInstance.fields[fieldIndex] = fieldValue
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }
}
