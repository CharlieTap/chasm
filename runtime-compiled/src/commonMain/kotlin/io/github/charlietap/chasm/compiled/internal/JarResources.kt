package io.github.charlietap.chasm.compiled.internal

import io.github.charlietap.chasm.declarations.DataSource
import java.io.InputStream
import java.util.jar.JarFile

internal class JarResources(
    private val jar: JarFile,
) : ModuleResources {

    override fun contains(path: String): Boolean {
        return jar.getJarEntry(path) != null
    }

    override fun length(path: String): Long? {
        return jar.getJarEntry(path)?.size
    }

    override fun open(path: String): InputStream? {
        return jar.getJarEntry(path)?.let(jar::getInputStream)
    }

    override fun data(path: String): DataSource {
        return DataSource {
            val stream = open(path) ?: throw IllegalStateException("resource $path is missing")
            StreamReader(path, stream)
        }
    }
}
