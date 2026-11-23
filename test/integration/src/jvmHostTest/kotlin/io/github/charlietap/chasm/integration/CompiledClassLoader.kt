package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.aot.CompiledModule
import io.github.charlietap.chasm.aot.MemoryClassOutput
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.URL
import java.net.URLConnection
import java.net.URLStreamHandler

class CompiledClassLoader(
    private val output: MemoryClassOutput,
) : ClassLoader(CompiledModule::class.java.classLoader) {

    fun module(className: String): CompiledModule {
        return loadClass(className).getField("COMPILED").get(null) as CompiledModule
    }

    override fun findClass(name: String): Class<*> {
        val bytes = output.classFiles[name] ?: throw ClassNotFoundException(name)
        return defineClass(name, bytes, 0, bytes.size)
    }

    override fun getResource(name: String): URL? {
        return findResource(name) ?: super.getResource(name)
    }

    override fun getResourceAsStream(name: String): InputStream? {
        return output.resources[name.removePrefix("/")]?.let(::ByteArrayInputStream) ?: super.getResourceAsStream(name)
    }

    @Suppress("DEPRECATION")
    override fun findResource(name: String): URL? {

        val path = name.removePrefix("/")
        val bytes = output.resources[path] ?: return null

        return URL(null, "$PROTOCOL:/$path", MemoryHandler(bytes))
    }

    private class MemoryHandler(
        private val bytes: ByteArray,
    ) : URLStreamHandler() {

        override fun openConnection(url: URL): URLConnection {
            return MemoryConnection(url, bytes)
        }
    }

    private class MemoryConnection(
        url: URL,
        private val bytes: ByteArray,
    ) : URLConnection(url) {

        override fun connect() {}

        override fun getInputStream(): InputStream {
            return ByteArrayInputStream(bytes)
        }
    }

    private companion object {
        const val PROTOCOL = "chasm-memory"
    }
}
