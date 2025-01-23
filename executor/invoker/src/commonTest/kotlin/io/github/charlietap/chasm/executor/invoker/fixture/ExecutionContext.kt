package io.github.charlietap.chasm.executor.invoker.fixture

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.config.runtimeConfig
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.stack.ControlStack
import io.github.charlietap.chasm.executor.runtime.stack.ValueStack
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.executor.runtime.stack.vstack
import io.github.charlietap.chasm.fixture.executor.runtime.store

fun executionContext(
    cstack: ControlStack = cstack(),
    vstack: ValueStack = vstack(),
    store: Store = store(),
    instance: ModuleInstance = moduleInstance(),
    config: RuntimeConfig = runtimeConfig(),
) = ExecutionContext(
    cstack = cstack,
    vstack = vstack,
    store = store,
    instance = instance,
    config = config,
)
