package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.extendSigned
import io.github.charlietap.chasm.runtime.ext.popI31
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

internal fun I31GetSignedExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.I31GetSigned,
) = I31GetExecutor(
    context = context,
    extender = UInt::extendSigned,
)

internal inline fun I31GetExecutor(
    context: ExecutionContext,
    crossinline extender: (UInt) -> Int,
) {
    val stack = context.vstack
    val value = stack.popI31()

    val extended = extender(value)

    stack.pushI32(extended)
}
