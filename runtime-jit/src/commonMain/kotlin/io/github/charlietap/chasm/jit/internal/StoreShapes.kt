package io.github.charlietap.chasm.jit.internal

import dalvik.system.InMemoryDexClassLoader
import io.github.charlietap.chasm.compiler.backend.abi.SHAPE_OWNER_CLASS
import io.github.charlietap.chasm.compiler.backend.abi.SHAPE_OWNER_ENGINE
import java.lang.ref.WeakReference
import java.nio.ByteBuffer

internal class StoreShapes(
    root: ClassLoader,
    private val owner: Any,
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
        loader.loadClass(SHAPE_OWNER_NAME).getField(SHAPE_OWNER_ENGINE).set(null, WeakReference(owner))
        newest = loader
        defined.addAll(classNames)

        return loader
    }

    fun loadClass(name: String): Class<*> {
        return newest.loadClass(name)
    }

    private companion object {
        val SHAPE_OWNER_NAME = SHAPE_OWNER_CLASS.removePrefix("L").removeSuffix(";").replace('/', '.')
    }
}
