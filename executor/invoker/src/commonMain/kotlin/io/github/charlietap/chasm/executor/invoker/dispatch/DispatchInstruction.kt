package io.github.charlietap.chasm.executor.invoker.dispatch

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor

internal inline fun <T> dispatchInstruction(
    instruction: T,
    crossinline executor: Executor<T>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
