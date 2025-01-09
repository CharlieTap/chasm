package io.github.charlietap.chasm.executor.runtime

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.runtime.store.Store

data class Configuration(
    val store: Store,
    val thread: Thread,
    val config: RuntimeConfig,
)
