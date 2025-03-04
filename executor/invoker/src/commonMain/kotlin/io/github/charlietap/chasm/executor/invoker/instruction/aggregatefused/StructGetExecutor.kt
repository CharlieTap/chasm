package io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.field
import io.github.charlietap.chasm.runtime.ext.struct
import io.github.charlietap.chasm.runtime.ext.toStructAddress
import io.github.charlietap.chasm.runtime.instruction.FusedAggregateInstruction

internal inline fun StructGetExecutor(
    context: ExecutionContext,
    instruction: FusedAggregateInstruction.StructGet,
) {
    val store = context.store
    val stack = context.vstack

    val address = instruction.address(stack).toStructAddress()
    val structInstance = store.struct(address)
    val fieldValue = structInstance.field(instruction.fieldIndex)

    instruction.destination(fieldValue, stack)
}
