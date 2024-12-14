package io.github.charlietap.chasm.executor.invoker.fixture

import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.stack
import io.github.charlietap.chasm.fixture.executor.runtime.store

fun executionContext(
    stack: Stack = stack(),
    store: Store = store(),
    instance: ModuleInstance = moduleInstance(),
) = ExecutionContext(
    stack = stack,
    store = store,
    instance = instance,
)
