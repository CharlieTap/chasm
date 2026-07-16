package io.github.charlietap.chasm.embedding.shapes

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.runtime.address.ComponentRootAddress
import io.github.charlietap.chasm.runtime.address.RuntimeResourceTypeAddress
import io.github.charlietap.chasm.runtime.address.StoreIdentity
import io.github.charlietap.chasm.runtime.component.index.PreparedComponentFunctionIndex
import io.github.charlietap.chasm.type.component.ComponentFunctionType
import io.github.charlietap.chasm.type.component.ComponentResourceTypeId

class ComponentFunction internal constructor(
    internal val config: RuntimeConfig,
    internal val store: StoreIdentity,
    internal val root: ComponentRootAddress,
    internal val function: PreparedComponentFunctionIndex,
    internal val type: ComponentFunctionType,
    internal val resourceTypes: Map<ComponentResourceTypeId, RuntimeResourceTypeAddress>,
) : ComponentExportable {

    override fun equals(other: Any?): Boolean {
        val otherFunction = other as? ComponentFunction ?: return false
        return store === otherFunction.store && root == otherFunction.root && function == otherFunction.function
    }

    override fun hashCode(): Int {
        var result = store.hashCode()
        result = 31 * result + root.hashCode()
        result = 31 * result + function.hashCode()
        return result
    }
}
