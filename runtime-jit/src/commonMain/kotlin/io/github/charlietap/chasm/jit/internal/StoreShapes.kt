package io.github.charlietap.chasm.jit.internal

import dalvik.system.InMemoryDexClassLoader
import io.github.charlietap.chasm.runtime.compiled.gc.ManagedReferenceBridge
import io.github.charlietap.chasm.runtime.compiled.gc.ShapeOwners
import io.github.charlietap.chasm.runtime.store.Store
import java.lang.ref.WeakReference
import java.nio.ByteBuffer
import java.util.WeakHashMap

internal class StoreShapes(
    root: ClassLoader,
    private val bridge: WeakReference<ManagedReferenceBridge>,
) {

    private val defined = HashSet<String>()

    @Volatile
    private var newest: ClassLoader = root

    val loader: ClassLoader
        get() = newest

    @Synchronized
    fun load(
        code: List<ByteArray>,
        shapes: ByteArray?,
        classNames: List<String>,
    ): ClassLoader {

        if (shapes == null || defined.containsAll(classNames)) {
            return InMemoryDexClassLoader(code.map(ByteBuffer::wrap).toTypedArray(), newest)
        }

        val loader = InMemoryDexClassLoader((listOf(shapes) + code).map(ByteBuffer::wrap).toTypedArray(), newest)
        ShapeOwners.register(loader, checkNotNull(bridge.get()))
        newest = loader
        defined.addAll(classNames)

        return loader
    }

    fun loadClass(name: String): Class<*> {
        return newest.loadClass(name)
    }

    companion object {

        private val stores = WeakHashMap<Store, StoreShapes>()

        @Synchronized
        fun of(
            store: Store,
            root: ClassLoader,
        ): StoreShapes {
            return stores.getOrPut(store) { StoreShapes(root, WeakReference(managedBridge(store))) }
        }
    }
}
