package io.github.charlietap.chasm.aot.internal

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.aot.CompiledModule
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.URL
import java.net.URLConnection
import java.net.URLStreamHandler
import java.util.concurrent.ConcurrentHashMap

/**
 * Loads a compiled module's classes and resources from memory, keyed by internal name and path. Defines its own classes
 * before asking [parent], so a class of the same name elsewhere never stands in for one, and lets go of each class's
 * bytes once it is defined.
 */
@InternalChasmApi
@OptIn(ExperimentalChasmApi::class)
class MemoryClassLoader(
    classes: Map<String, ByteArray>,
    private val resources: Map<String, ByteArray>,
    parent: ClassLoader?,
) : ClassLoader(parent) {

    private val classes = ConcurrentHashMap(classes)

    fun module(
        className: String,
    ): CompiledModule = loadClass(className).getField(INSTANCE_FIELD).get(null) as CompiledModule

    override fun loadClass(
        name: String,
        resolve: Boolean,
    ): Class<*> = synchronized(getClassLoadingLock(name)) {
        val loaded = findLoadedClass(name)
            ?: classes.remove(name.replace('.', '/'))?.let { bytes -> defineClass(name, bytes, 0, bytes.size) }
            ?: return super.loadClass(name, resolve)
        if (resolve) resolveClass(loaded)
        loaded
    }

    override fun getResource(
        name: String,
    ): URL? = findResource(name) ?: super.getResource(name)

    override fun getResourceAsStream(
        name: String,
    ): InputStream? = resources[name.removePrefix("/")]?.let(::ByteArrayInputStream) ?: super.getResourceAsStream(name)

    @Suppress("DEPRECATION")
    override fun findResource(
        name: String,
    ): URL? {
        val path = name.removePrefix("/")
        val bytes = resources[path] ?: return null
        return URL(null, "$PROTOCOL:/$path", MemoryHandler(bytes))
    }

    private class MemoryHandler(
        private val bytes: ByteArray,
    ) : URLStreamHandler() {

        override fun openConnection(
            url: URL,
        ): URLConnection = MemoryConnection(url, bytes)
    }

    private class MemoryConnection(
        url: URL,
        private val bytes: ByteArray,
    ) : URLConnection(url) {

        override fun connect() = Unit

        override fun getInputStream(): InputStream = ByteArrayInputStream(bytes)
    }

    private companion object {
        const val INSTANCE_FIELD = "INSTANCE"
        const val PROTOCOL = "chasm-memory"

        // Lets several threads define a module's classes at once rather than in turn on the loader's lock.
        init {
            registerAsParallelCapable()
        }
    }
}
