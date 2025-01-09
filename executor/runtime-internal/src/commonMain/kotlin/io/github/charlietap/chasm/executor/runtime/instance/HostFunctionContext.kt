package io.github.charlietap.chasm.executor.runtime.instance

import io.github.charlietap.chasm.executor.runtime.store.Store

data class HostFunctionContext(
    val store: Store,
    val instance: ModuleInstance,
)
