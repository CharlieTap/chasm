package io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.array
import io.github.charlietap.chasm.executor.runtime.ext.toArrayAddress
import io.github.charlietap.chasm.executor.runtime.instruction.FusedAggregateInstruction

internal fun ArrayLenExecutor(
    context: ExecutionContext,
    instruction: FusedAggregateInstruction.ArrayLen,
) {
    val store = context.store
    val stack = context.vstack
    val address = instruction.address(stack).toArrayAddress()
    val arrayInstance = store.array(address)

    instruction.destination(arrayInstance.fields.size.toLong(), stack)
}
