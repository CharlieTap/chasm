package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.array
import io.github.charlietap.chasm.executor.runtime.ext.field
import io.github.charlietap.chasm.executor.runtime.ext.popArrayReference
import io.github.charlietap.chasm.executor.runtime.ext.pushExecution
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction

internal inline fun ArrayGetExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayGet,
) {
    val store = context.store
    val stack = context.vstack

    val fieldIndex = stack.popI32()
    val arrayRef = stack.popArrayReference()
    val arrayInstance = store.array(arrayRef.address)
    val fieldValue = arrayInstance.field(fieldIndex)

    stack.pushExecution(fieldValue.executionValue)
}
