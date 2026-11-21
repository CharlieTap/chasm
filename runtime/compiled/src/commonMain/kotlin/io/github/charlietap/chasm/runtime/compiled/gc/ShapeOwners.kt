package io.github.charlietap.chasm.runtime.compiled.gc

import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

class ShapeOwners private constructor() {

    companion object {

        private val owners = ConcurrentHashMap<ClassLoader, WeakReference<ManagedReferenceBridge>>()

        fun register(
            loader: ClassLoader,
            bridge: ManagedReferenceBridge,
        ) {
            owners.values.removeIf { owner -> owner.get() == null }
            owners[loader] = WeakReference(bridge)
        }

        fun owner(shape: Class<*>): Any {
            val bridge = owners[shape.classLoader]?.get()
            return checkNotNull(bridge) { "the store of this ${shape.simpleName} is no longer alive" }.engine
        }
    }
}
