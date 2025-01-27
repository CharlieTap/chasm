package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.array
import io.github.charlietap.chasm.executor.runtime.ext.field
import io.github.charlietap.chasm.executor.runtime.ext.popArrayReference
import io.github.charlietap.chasm.executor.runtime.ext.pushExecution
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction

internal fun ArrayGetUnsignedExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayGetUnsigned,
) = ArrayGetUnsignedExecutor(
    context = context,
    fieldUnpacker = ::FieldUnpacker,
)

internal inline fun ArrayGetUnsignedExecutor(
    context: ExecutionContext,
    crossinline fieldUnpacker: FieldUnpacker,
) {
    val store = context.store
    val stack = context.vstack

    val fieldIndex = stack.popI32()
    val arrayRef = stack.popArrayReference()
    val arrayInstance = store.array(arrayRef.address)
    val fieldType = arrayInstance.arrayType.fieldType

    val fieldValue = arrayInstance.field(fieldIndex)
    val unpackedValue = fieldUnpacker(fieldValue, fieldType, false)

    stack.pushExecution(unpackedValue)
}
