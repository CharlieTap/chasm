package io.github.charlietap.chasm.embedding.shapes

import io.github.charlietap.chasm.runtime.component.store.ComponentStore
import io.github.charlietap.chasm.runtime.store.identity
import io.github.charlietap.chasm.runtime.store.Store as InternalStore

class Store internal constructor(
    internal val store: InternalStore = InternalStore(),
) {
    internal var components: ComponentStore? = null

    internal val identity
        get() = store.identity()

    internal fun componentStore(): ComponentStore = components
        ?: ComponentStore().also { componentStore -> components = componentStore }
}
