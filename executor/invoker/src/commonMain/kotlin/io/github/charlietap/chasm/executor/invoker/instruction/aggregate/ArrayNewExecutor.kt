package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun ArrayNewExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayNew,
) {
    val size = vstack.peekNthI32(0)
    val value = vstack.peekNthI64(1)
    val reference = try {
        context.heap.allocateArrayFilled(context, instruction.rtt, size, value)
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }
    vstack.popI32()
    vstack.pop()
    vstack.push(reference)
}
