package io.github.charlietap.chasm.fixture.executor.instantiator

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.config.runtimeConfig
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.fixture.ir.module.module
import io.github.charlietap.chasm.ir.module.Module

fun instantiationContext(
    store: Store = store(),
    module: Module = module(),
    config: RuntimeConfig = runtimeConfig(),
    instance: ModuleInstance? = null,
): InstantiationContext = InstantiationContext(
    store = store,
    module = module,
    config = config,
    instance = instance,
)
