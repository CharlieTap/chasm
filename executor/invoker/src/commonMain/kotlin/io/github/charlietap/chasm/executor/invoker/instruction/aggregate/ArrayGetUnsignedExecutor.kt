package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.array
import io.github.charlietap.chasm.runtime.ext.packedField
import io.github.charlietap.chasm.runtime.ext.popArrayAddress
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

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
    val address = stack.popArrayAddress()
    val arrayInstance = store.array(address)

    val (packed, type) = arrayInstance.packedField(fieldIndex)
    val unpackedValue = fieldUnpacker(packed, type, false)

    stack.push(unpackedValue)
}
