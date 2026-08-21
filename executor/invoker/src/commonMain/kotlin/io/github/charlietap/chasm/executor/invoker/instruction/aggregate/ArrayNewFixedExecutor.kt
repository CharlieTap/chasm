package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.RTT

internal fun ArrayNewFixedExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayNewFixed,
) = ArrayNewFixedExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    rtt = instruction.rtt,
    length = instruction.length.toInt(),
)

internal inline fun ArrayNewFixedExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    rtt: RTT,
    length: Int,
) {
    context.heap.allocateArrayFromStack(context, rtt, length)
}
