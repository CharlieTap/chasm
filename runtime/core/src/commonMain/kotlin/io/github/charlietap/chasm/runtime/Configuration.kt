package io.github.charlietap.chasm.runtime

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.runtime.store.Store

data class Configuration(
    val store: Store,
    val thread: Thread,
    val config: RuntimeConfig,
)
