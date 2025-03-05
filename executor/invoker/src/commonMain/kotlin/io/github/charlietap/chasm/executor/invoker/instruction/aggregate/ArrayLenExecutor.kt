package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.array
import io.github.charlietap.chasm.runtime.ext.popArrayAddress
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

internal fun ArrayLenExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayLen,
) {
    val store = context.store
    val stack = context.vstack
    val address = stack.popArrayAddress()
    val arrayInstance = store.array(address)

    stack.pushI32(arrayInstance.fields.size)
}
