package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.function.returnToCaller
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal fun ReturnExecutor(
    vstack: ValueStack,
    store: Store,
    resultCount: Int,
    activationHeaderSlot: Int,
): Int = returnToCaller(vstack, store, resultCount, activationHeaderSlot)
