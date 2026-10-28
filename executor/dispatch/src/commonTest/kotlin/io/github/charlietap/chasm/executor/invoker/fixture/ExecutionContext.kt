package io.github.charlietap.chasm.executor.invoker.fixture

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.fixture.config.runtimeConfig
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.stack.vstack
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

fun executionContext(
    vstack: ValueStack = vstack(),
    store: Store = store(),
    instance: ModuleInstance = moduleInstance(),
    config: RuntimeConfig = runtimeConfig(),
) = ExecutionContext(
    vstack = vstack,
    store = store,
    instance = instance,
    config = config,
)
