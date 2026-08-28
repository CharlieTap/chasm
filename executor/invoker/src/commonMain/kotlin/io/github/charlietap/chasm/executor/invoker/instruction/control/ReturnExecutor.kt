package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.function.returnToCaller
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal fun ReturnExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    resultCount: Int,
    activationHeaderSlot: Int,
): Int = returnToCaller(vstack, context.store, resultCount, activationHeaderSlot)
