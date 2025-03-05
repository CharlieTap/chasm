package io.github.charlietap.chasm.fixture.executor.runtime.execution

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.config.runtimeConfig
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.executor.runtime.stack.vstack
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

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
