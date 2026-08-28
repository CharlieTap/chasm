package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun ArrayGetExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayGet,
) {
    val fieldIndex = vstack.popI32()
    val reference = vstack.pop()
    val fieldValue = context.heap.getArrayElementTrusted(reference, fieldIndex)

    vstack.push(fieldValue)
}
