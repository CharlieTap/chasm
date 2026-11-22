package io.github.charlietap.chasm.embedding.fixture

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.diagnostic.TrapSource
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.internal.StoreRuntime
import io.github.charlietap.chasm.embedding.internal.interpreterEngine
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

internal fun interpreterStore(
    runtime: RuntimeStore = store(),
    trapSource: (ModuleInstance) -> TrapSource? = { null },
): Store {
    return interpreterEngine(StoreRuntime(runtime), DecodedModuleInstaller(trapSource)).store
}

internal suspend fun decodedModule(bytes: ByteArray): Module {
    return module(bytes).expect("the fixture module decodes")
}

internal suspend fun instantiate(
    store: Store,
    bytes: ByteArray,
    imports: List<Import> = emptyList(),
    config: RuntimeConfig = RuntimeConfig(),
): Instance {
    return instance(store, decodedModule(bytes), imports, config).expect("the fixture module instantiates")
}
