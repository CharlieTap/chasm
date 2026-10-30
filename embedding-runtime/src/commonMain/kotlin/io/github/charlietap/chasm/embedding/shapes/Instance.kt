package io.github.charlietap.chasm.embedding.shapes

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.exports
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store

class Instance internal constructor(
    internal val config: RuntimeConfig,
    @property:InternalChasmApi val instance: ModuleInstance,
    internal val store: Store,
) {
    val exports by lazy {
        exports(this)
    }

    internal val exportsByName: Map<String, ExternalValue> by lazy {
        instance.exports.associateBy({ export -> export.name.name }, { export -> export.value })
    }
}
