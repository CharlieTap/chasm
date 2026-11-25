package io.github.charlietap.chasm.compiled.internal

import io.github.charlietap.chasm.declarations.DataSource
import java.io.ByteArrayInputStream
import java.io.InputStream

internal class MemoryResources(
    private val resources: Map<String, ByteArray>,
) : ModuleResources {

    override fun contains(path: String): Boolean {
        return path in resources
    }

    override fun length(path: String): Long? {
        return resources[path]?.size?.toLong()
    }

    override fun open(path: String): InputStream? {
        return resources[path]?.let(::ByteArrayInputStream)
    }

    override fun data(path: String): DataSource {
        return DataSource {
            val bytes = resources[path] ?: throw IllegalStateException("resource $path is missing")
            ByteArrayDataReader(bytes)
        }
    }
}
