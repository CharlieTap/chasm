package io.github.charlietap.chasm.runtime.compiled.gc

import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

class ShapeOwners private constructor() {

    companion object {

        private val owners = ConcurrentHashMap<ClassLoader, WeakReference<ManagedStore>>()

        fun register(
            loader: ClassLoader,
            managedStore: ManagedStore,
        ) {
            owners.values.removeIf { owner -> owner.get() == null }
            owners[loader] = WeakReference(managedStore)
        }

        fun owner(shape: Class<*>): Any {
            val managedStore = owners[shape.classLoader]?.get()
            return checkNotNull(managedStore) { "the store of this ${shape.simpleName} is no longer alive" }.engine
        }
    }
}
