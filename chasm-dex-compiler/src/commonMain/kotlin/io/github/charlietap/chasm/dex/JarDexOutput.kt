package io.github.charlietap.chasm.dex

import io.github.charlietap.chasm.compiled.format.DATA_EXTENSION
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream
import java.util.zip.CRC32
import java.util.zip.ZipEntry

internal class JarDexOutput(
    private val jar: Path,
) : AtomicDexOutput {

    private var temporary: Path? = null
    private var stream: JarOutputStream? = null

    override fun dexFile(
        name: String,
        bytes: ByteArray,
    ) {
        entry(name, bytes, stored = false)
    }

    override fun resource(
        path: String,
        bytes: ByteArray,
    ) {
        entry(path, bytes, stored = path.endsWith(".$DATA_EXTENSION"))
    }

    override fun commit() {

        val jarStream = stream ?: open()
        jarStream.close()
        Files.move(checkNotNull(temporary), jar, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)

        temporary = null
        stream = null
    }

    override fun discard() {

        val _ = runCatching { stream?.close() }
        temporary?.let { staged ->
            val _ = runCatching { Files.deleteIfExists(staged) }
        }

        temporary = null
        stream = null
    }

    private fun entry(
        name: String,
        bytes: ByteArray,
        stored: Boolean,
    ) {

        val jarEntry = JarEntry(name).apply { time = ENTRY_TIME.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() }
        if (stored) {
            jarEntry.method = ZipEntry.STORED
            jarEntry.size = bytes.size.toLong()
            jarEntry.compressedSize = bytes.size.toLong()
            jarEntry.crc = CRC32().apply { update(bytes) }.value
        }

        val jarStream = stream ?: open()
        jarStream.putNextEntry(jarEntry)
        jarStream.write(bytes)
        jarStream.closeEntry()
    }

    private fun open(): JarOutputStream {

        val directory = jar.toAbsolutePath().parent
        Files.createDirectories(directory)

        val staged = Files.createTempFile(directory, ".${jar.fileName}-", ".tmp")
        temporary = staged

        val output = Files.newOutputStream(staged)
        if (!staged.toFile().setReadOnly()) {
            output.close()
            throw IOException("$staged cannot be made read-only")
        }

        val jarStream = JarOutputStream(output.buffered())
        stream = jarStream

        return jarStream
    }

    private companion object {
        val ENTRY_TIME: LocalDateTime = LocalDateTime.of(1980, 2, 1, 0, 0)
    }
}
