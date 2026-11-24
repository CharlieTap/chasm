@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.aot

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.aot.internal.AotInstantiator
import io.github.charlietap.chasm.aot.internal.AotModule
import io.github.charlietap.chasm.embedding.internal.StoreRuntime
import io.github.charlietap.chasm.embedding.managed.ManagedStoreEngine
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes._payload
import io.github.charlietap.chasm.runtime.compiled.gc.ManagedStore
import io.github.charlietap.chasm.runtime.compiled.gc.RttRegistry
import io.github.charlietap.chasm.runtime.compiled.gc.RttTypes
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

fun store(module: Module): Store {

    val payload = requireNotNull(module._payload() as? AotModule) { "the module was not loaded by module(CompiledModule)" }
    val storeRuntime = StoreRuntime(RuntimeStore(config = payload.config))
    val managedStore = ManagedStore(storeRuntime.liveStore()) { store -> RttTypes(store, RttRegistry(store)) }
    val registry = (managedStore.types as RttTypes).registry

    return ManagedStoreEngine(storeRuntime, managedStore) { instances ->
        AotInstantiator(payload, storeRuntime, managedStore, registry, instances)
    }.store
}
