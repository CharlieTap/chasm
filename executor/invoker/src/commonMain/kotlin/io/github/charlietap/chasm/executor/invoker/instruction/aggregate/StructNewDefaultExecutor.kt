package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun StructNewDefaultExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: AggregateInstruction.StructNewDefault,
) {
    vstack.push(context.heap.allocateStruct(context, instruction.rtt, instruction.fields))
}
