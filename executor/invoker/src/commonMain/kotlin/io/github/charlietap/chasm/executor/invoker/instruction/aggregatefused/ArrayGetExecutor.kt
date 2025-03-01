package io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.array
import io.github.charlietap.chasm.executor.runtime.ext.field
import io.github.charlietap.chasm.executor.runtime.ext.toArrayAddress
import io.github.charlietap.chasm.executor.runtime.instruction.FusedAggregateInstruction

internal inline fun ArrayGetExecutor(
    context: ExecutionContext,
    instruction: FusedAggregateInstruction.ArrayGet,
) {
    val store = context.store
    val stack = context.vstack

    val fieldIndex = instruction.field(stack).toInt()
    val address = instruction.address(stack).toArrayAddress()
    val arrayInstance = store.array(address)
    val fieldValue = arrayInstance.field(fieldIndex)

    instruction.destination(fieldValue, stack)
}
