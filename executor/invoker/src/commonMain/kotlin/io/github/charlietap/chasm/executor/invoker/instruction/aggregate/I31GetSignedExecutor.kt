package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.extendSigned
import io.github.charlietap.chasm.runtime.ext.popI31
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal fun I31GetSignedExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateInstruction.I31GetSigned,
) = I31GetExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    extender = UInt::extendSigned,
)

internal inline fun I31GetExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    crossinline extender: (UInt) -> Int,
) {
    val value = vstack.popI31()
    val extended = extender(value)

    vstack.pushI32(extended)
}
