package io.github.charlietap.chasm.embedding.fixture

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.fixture.config.runtimeConfig
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store

fun publicInstance(
    config: RuntimeConfig = runtimeConfig(),
    moduleInstance: ModuleInstance = moduleInstance(),
    store: Store = store(),
): Instance = Instance(
    config = config,
    instance = moduleInstance,
    store = store,
)
