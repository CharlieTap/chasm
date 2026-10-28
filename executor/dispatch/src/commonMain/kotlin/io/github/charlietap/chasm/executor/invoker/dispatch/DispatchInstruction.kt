package io.github.charlietap.chasm.executor.invoker.dispatch

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun dispatchInstruction(
    crossinline execute: (
        vstack: ValueStack,
        context: ExecutionContext,
    ) -> Unit,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    execute(vstack, context)
    nextIp
}
