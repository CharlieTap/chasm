package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun StructNewExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: AggregateInstruction.StructNew,
) {
    context.heap.allocateStructFromStack(context, instruction.rtt, instruction.fieldCount)
}
