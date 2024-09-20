package io.github.charlietap.chasm.executor.invoker.fixture

import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.store

fun executionContext(
    stack: Stack = stack(),
    store: Store = store(),
) = ExecutionContext(
    stack = stack,
    store = store,
)
