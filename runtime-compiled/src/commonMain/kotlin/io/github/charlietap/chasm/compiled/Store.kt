package io.github.charlietap.chasm.compiled

import io.github.charlietap.chasm.compiled.internal.CompiledInstantiator
import io.github.charlietap.chasm.compiled.internal.CompiledModulePayload
import io.github.charlietap.chasm.compiled.internal.StoreModuleNames
import io.github.charlietap.chasm.embedding.compiled.internal.ManagedStoreEngine
import io.github.charlietap.chasm.embedding.internal.StoreRuntime
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes._payload
import io.github.charlietap.chasm.runtime.compiled.gc.ManagedStore
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

fun store(module: Module): Store {

    val payload = requireNotNull(module._payload() as? CompiledModulePayload) { "the module was not loaded by runtime-compiled" }
    val storeRuntime = StoreRuntime(RuntimeStore(config = payload.config))
    val managedStore = ManagedStore(storeRuntime.liveStore())

    return ManagedStoreEngine(storeRuntime, managedStore) { instances ->
        CompiledInstantiator(payload, storeRuntime, managedStore, managedStore.types.registry, instances, StoreModuleNames())
    }.store
}
