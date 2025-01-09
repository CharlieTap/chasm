package io.github.charlietap.chasm.fixture.executor.instantiator

import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.config.runtimeConfig
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.fixture.executor.runtime.store

fun instantiationContext(
    store: Store = store(),
    module: Module = module(),
    config: RuntimeConfig = runtimeConfig(),
): InstantiationContext = InstantiationContext(
    store = store,
    module = module,
    config = config,
)
