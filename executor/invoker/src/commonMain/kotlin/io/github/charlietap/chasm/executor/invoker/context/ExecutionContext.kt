package io.github.charlietap.chasm.executor.invoker.context

import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.store.Store

data class ExecutionContext(
    val stack: Stack,
    val store: Store,
)
