package io.github.charlietap.chasm.runtime.instance

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.runtime.store.Store

data class HostFunctionContext(
    val config: RuntimeConfig,
    val store: Store,
    val instance: ModuleInstance,
)
