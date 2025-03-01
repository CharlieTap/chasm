package io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.FieldUnpacker
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.packedField
import io.github.charlietap.chasm.executor.runtime.ext.struct
import io.github.charlietap.chasm.executor.runtime.ext.toStructAddress
import io.github.charlietap.chasm.executor.runtime.instruction.FusedAggregateInstruction

internal inline fun StructGetSignedExecutor(
    context: ExecutionContext,
    instruction: FusedAggregateInstruction.StructGetSigned,
) = StructGetSignedExecutor(
    context = context,
    instruction = instruction,
    fieldUnpacker = ::FieldUnpacker,
)

internal inline fun StructGetSignedExecutor(
    context: ExecutionContext,
    instruction: FusedAggregateInstruction.StructGetSigned,
    crossinline fieldUnpacker: FieldUnpacker,
) {
    val store = context.store
    val stack = context.vstack

    val address = instruction.address(stack).toStructAddress()
    val structInstance = store.struct(address)

    val (packed, type) = structInstance.packedField(instruction.fieldIndex)
    val unpackedValue = fieldUnpacker(packed, type, true)

    instruction.destination(unpackedValue, stack)
}
