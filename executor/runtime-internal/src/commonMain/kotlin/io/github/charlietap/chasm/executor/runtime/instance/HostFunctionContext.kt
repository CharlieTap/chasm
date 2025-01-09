package io.github.charlietap.chasm.executor.runtime.instance

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.runtime.store.Store

data class HostFunctionContext(
    val config: RuntimeConfig,
    val store: Store,
    val instance: ModuleInstance,
)
