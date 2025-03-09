package io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.FieldUnpacker
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.array
import io.github.charlietap.chasm.runtime.ext.packedField
import io.github.charlietap.chasm.runtime.ext.toArrayAddress
import io.github.charlietap.chasm.runtime.instruction.FusedAggregateInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun ArrayGetUnsignedExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedAggregateInstruction.ArrayGetUnsigned,
) = ArrayGetUnsignedExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    fieldUnpacker = ::FieldUnpacker,
)

internal inline fun ArrayGetUnsignedExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedAggregateInstruction.ArrayGetUnsigned,
    crossinline fieldUnpacker: FieldUnpacker,
) {
    val fieldIndex = instruction.field(vstack).toInt()
    val address = instruction.address(vstack).toArrayAddress()
    val arrayInstance = store.array(address)

    val (packed, type) = arrayInstance.packedField(fieldIndex)
    val unpackedValue = fieldUnpacker(packed, type, false)

    instruction.destination(unpackedValue, vstack)
}
