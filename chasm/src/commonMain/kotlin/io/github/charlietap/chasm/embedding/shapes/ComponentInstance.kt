package io.github.charlietap.chasm.embedding.shapes

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.runtime.address.ComponentRootAddress
import io.github.charlietap.chasm.runtime.address.StoreIdentity

class ComponentInstance internal constructor(
    internal val config: RuntimeConfig,
    internal val store: StoreIdentity,
    internal val root: ComponentRootAddress,
    val exports: List<ComponentExport>,
)
