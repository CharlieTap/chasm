package io.github.charlietap.chasm.jit.internal

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.internal.StoreRuntime
import io.github.charlietap.chasm.embedding.managed.ManagedStoreEngine
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.executor.instantiator.compat.CompatibilityChecker
import io.github.charlietap.chasm.runtime.compiled.gc.ManagedStore
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

@InternalChasmApi
fun _jitStore(config: RuntimeConfig): Store {

    val storeRuntime = StoreRuntime(RuntimeStore(config = config))
    val runtime = storeRuntime.liveStore()
    val accessors = GeneratedShapeAccessors()
    val managedStore = ManagedStore(runtime) { store -> ShapeClassTypes(store, runtime.heap, accessors) }
    val shapeTypes = managedStore.types as ShapeClassTypes

    return ManagedStoreEngine(storeRuntime, managedStore) { instances ->
        JitInstantiator(storeRuntime, managedStore, shapeTypes, instances, ::CompatibilityChecker)
    }.store
}
