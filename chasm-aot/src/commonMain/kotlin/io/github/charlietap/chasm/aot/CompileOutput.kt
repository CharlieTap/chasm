package io.github.charlietap.chasm.aot

import io.github.charlietap.chasm.ExperimentalChasmApi
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.URL
import java.net.URLConnection
import java.net.URLStreamHandler
import java.nio.file.Path

/** Where [compile] puts a module's class files and resources. */
sealed interface CompileOutput {

    /** Keeps compiled modules in memory. Not safe to use from several threads at once. */
    class Memory : CompileOutput {

        internal val classFiles = LinkedHashMap<String, ByteArray>()
        internal val resourceFiles = LinkedHashMap<String, ByteArray>()

        // Keyed by internal class name, such as com/example/parser/WasmModule.
        val classes: Map<String, ByteArray>
            get() = classFiles

        val resources: Map<String, ByteArray>
            get() = resourceFiles

        /** Loads the compiled module on this JVM, in a class loader of its own. */
        @ExperimentalChasmApi
        fun load(
            artifact: CompiledModuleArtifact,
        ): CompiledModule = MemoryClassLoader(classFiles.copied(), resourceFiles.copied()).module(artifact.moduleClassName)

        /** The loader reads its bytes lazily, so it keeps copies callers cannot change underneath it. */
        private fun Map<String, ByteArray>.copied(): Map<String, ByteArray> = mapValues { (_, bytes) -> bytes.copyOf() }
    }

    /** Writes the class files and resources under [path], laid out as a classes directory on a classpath. */
    class Directory(
        val path: Path,
    ) : CompileOutput

    /**
     * Writes the module into the jar at [path], continuing in `name-2.jar`, `name-3.jar` and so on beside it past the
     * entries one jar holds. Replaces the jars an earlier compilation left there.
     */
    class Jar(
        val path: Path,
    ) : CompileOutput
}

/** Defines generated classes before asking its parent, so a class of the same name elsewhere never stands in for one. */
@OptIn(ExperimentalChasmApi::class)
private class MemoryClassLoader(
    private val classes: Map<String, ByteArray>,
    private val resources: Map<String, ByteArray>,
) : ClassLoader(CompiledModule::class.java.classLoader) {

    fun module(
        className: String,
    ): CompiledModule = loadClass(className).getField(INSTANCE_FIELD).get(null) as CompiledModule

    override fun loadClass(
        name: String,
        resolve: Boolean,
    ): Class<*> = synchronized(getClassLoadingLock(name)) {
        val loaded = findLoadedClass(name) ?: classes[name.replace('.', '/')]?.let { bytes ->
            defineClass(name, bytes, 0, bytes.size)
        }
        if (loaded == null) return super.loadClass(name, resolve)
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
    }
}
