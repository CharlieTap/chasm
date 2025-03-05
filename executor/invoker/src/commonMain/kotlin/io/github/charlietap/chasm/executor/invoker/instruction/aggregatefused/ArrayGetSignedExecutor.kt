package io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.FieldUnpacker
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.array
import io.github.charlietap.chasm.runtime.ext.packedField
import io.github.charlietap.chasm.runtime.ext.toArrayAddress
import io.github.charlietap.chasm.runtime.instruction.FusedAggregateInstruction

internal inline fun ArrayGetSignedExecutor(
    context: ExecutionContext,
    instruction: FusedAggregateInstruction.ArrayGetSigned,
) = ArrayGetSignedExecutor(
    context = context,
    instruction = instruction,
    fieldUnpacker = ::FieldUnpacker,
)

internal inline fun ArrayGetSignedExecutor(
    context: ExecutionContext,
    instruction: FusedAggregateInstruction.ArrayGetSigned,
    crossinline fieldUnpacker: FieldUnpacker,
) {
    val store = context.store
    val stack = context.vstack

    val fieldIndex = instruction.field(stack).toInt()
    val address = instruction.address(stack).toArrayAddress()
    val arrayInstance = store.array(address)

    val (packed, type) = arrayInstance.packedField(fieldIndex)
    val unpackedValue = fieldUnpacker(packed, type, true)

    instruction.destination(unpackedValue, stack)
}
