package io.github.charlietap.chasm.embedding.fixture

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.shapes.ComponentExport
import io.github.charlietap.chasm.embedding.shapes.ComponentInstance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.fixture.config.runtimeConfig
import io.github.charlietap.chasm.fixture.runtime.component.address.componentRootAddress
import io.github.charlietap.chasm.runtime.address.ComponentRootAddress

fun publicComponentInstance(
    store: Store = publicStore(),
    config: RuntimeConfig = runtimeConfig(),
    root: ComponentRootAddress = componentRootAddress(),
    exports: List<ComponentExport> = emptyList(),
): ComponentInstance = ComponentInstance(
    config = config,
    store = store.identity,
    root = root,
    exports = exports,
)
