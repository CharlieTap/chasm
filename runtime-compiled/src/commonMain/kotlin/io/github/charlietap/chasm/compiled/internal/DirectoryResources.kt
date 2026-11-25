package io.github.charlietap.chasm.compiled.internal

import io.github.charlietap.chasm.declarations.DataSource
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

internal class DirectoryResources(
    private val directory: Path,
) : ModuleResources {

    private val opened = HashMap<String, ByteBuffer>()

    override fun contains(path: String): Boolean {
        return mapped(path) != null
    }

    override fun length(path: String): Long? {
        return mapped(path)?.capacity()?.toLong()
    }

    override fun open(path: String): InputStream? {
        return mapped(path)?.let(::MappedInputStream)
    }

    override fun data(path: String): DataSource {
        return DataSource {
            val bytes = mapped(path) ?: throw IllegalStateException("resource $path is missing")
            MappedReader(path, bytes)
        }
    }

    @Synchronized
    private fun mapped(path: String): ByteBuffer? {

        opened[path]?.let { bytes -> return bytes }

        val file = directory.resolve(path)
        if (!Files.isRegularFile(file)) {
            return null
        }

        val bytes = FileChannel.open(file, StandardOpenOption.READ).use { channel ->
            val size = channel.size()
            if (size > Int.MAX_VALUE) {
                throw IOException("$file holds $size bytes, more than one mapping can hold")
            }
            channel.map(FileChannel.MapMode.READ_ONLY, 0, size)
        }
        opened[path] = bytes

        return bytes
    }
}
