package io.github.charlietap.chasm.embedding.shapes

import io.github.charlietap.chasm.runtime.address.RuntimeResourceTypeAddress
import io.github.charlietap.chasm.runtime.address.StoreIdentity

data class ComponentExport(
    val name: String,
    val value: ComponentExportable,
)

sealed interface ComponentExportable : ComponentImportable

data class ComponentExportInstance(
    val exports: List<ComponentExport>,
) : ComponentExportable

class ComponentResourceType internal constructor(
    internal val store: StoreIdentity,
    internal val address: RuntimeResourceTypeAddress,
) : ComponentExportable
