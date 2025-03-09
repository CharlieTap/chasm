package io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.FieldUnpacker
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.packedField
import io.github.charlietap.chasm.runtime.ext.struct
import io.github.charlietap.chasm.runtime.ext.toStructAddress
import io.github.charlietap.chasm.runtime.instruction.FusedAggregateInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun StructGetSignedExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedAggregateInstruction.StructGetSigned,
) = StructGetSignedExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    fieldUnpacker = ::FieldUnpacker,
)

internal inline fun StructGetSignedExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedAggregateInstruction.StructGetSigned,
    crossinline fieldUnpacker: FieldUnpacker,
) {
    val address = instruction.address(vstack).toStructAddress()
    val structInstance = store.struct(address)

    val (packed, type) = structInstance.packedField(instruction.fieldIndex)
    val unpackedValue = fieldUnpacker(packed, type, true)

    instruction.destination(unpackedValue, vstack)
}
