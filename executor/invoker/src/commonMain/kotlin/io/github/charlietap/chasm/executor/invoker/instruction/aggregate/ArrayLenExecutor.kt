package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.array
import io.github.charlietap.chasm.executor.runtime.ext.popArrayReference
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction

internal fun ArrayLenExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayLen,
) {
    val store = context.store
    val stack = context.vstack
    val arrayRef = stack.popArrayReference()
    val arrayInstance = store.array(arrayRef.address)

    stack.pushI32(arrayInstance.fields.size)
}
