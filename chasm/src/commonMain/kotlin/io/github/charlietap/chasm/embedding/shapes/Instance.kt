package io.github.charlietap.chasm.embedding.shapes

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.exports
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store

class Instance internal constructor(
    internal val config: RuntimeConfig,
    internal val instance: ModuleInstance,
    internal val store: Store,
) {
    val exports by lazy {
        exports(this)
    }
}
