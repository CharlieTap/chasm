package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.type.RTT

internal fun ArrayNewFixedExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayNewFixed,
) = ArrayNewFixedExecutor(
    vstack = vstack,
    context = context,
    rtt = instruction.rtt,
    length = instruction.length.toInt(),
)

internal inline fun ArrayNewFixedExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    rtt: RTT,
    length: Int,
) {
    context.heap.allocateArrayFromStack(context, rtt, length)
}
