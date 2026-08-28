package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun StructSetExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: AggregateInstruction.StructSet,
) {
    val executionValue = vstack.pop()
    val reference = vstack.pop()
    context.heap.setStructFieldTrusted(reference, instruction.fieldIndex, executionValue)
}
