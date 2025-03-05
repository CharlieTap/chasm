package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.array
import io.github.charlietap.chasm.runtime.ext.field
import io.github.charlietap.chasm.runtime.ext.popArrayAddress
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

internal inline fun ArrayGetExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayGet,
) {
    val store = context.store
    val stack = context.vstack

    val fieldIndex = stack.popI32()
    val address = stack.popArrayAddress()
    val arrayInstance = store.array(address)
    val fieldValue = arrayInstance.field(fieldIndex)

    stack.push(fieldValue)
}
