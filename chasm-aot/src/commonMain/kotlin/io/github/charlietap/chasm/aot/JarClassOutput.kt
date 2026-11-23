package io.github.charlietap.chasm.aot

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.time.LocalDateTime
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream
import java.util.zip.CRC32
import java.util.zip.ZipEntry

internal class JarClassOutput(
    private val jar: Path,
) : StagedClassOutput {

    private var temporary: Path? = null
    private var stream: JarOutputStream? = null
    private var entryCount = 0

    override fun classFile(
        className: String,
        bytes: ByteArray,
    ) {
        entry("${className.replace('.', '/')}.class", bytes, stored = false)
    }

    override fun resource(
        path: String,
        bytes: ByteArray,
    ) {
        entry(path, bytes, stored = path.endsWith(".chasmdata"))
    }

    override fun commit() {

        val jarStream = stream ?: open()
        jarStream.close()
        Files.move(checkNotNull(temporary), jar, StandardCopyOption.REPLACE_EXISTING)

        temporary = null
        stream = null
        entryCount = 0
    }

    override fun discard() {

        val _ = runCatching { stream?.close() }
        temporary?.let { staged ->
            val _ = runCatching { Files.deleteIfExists(staged) }
        }

        temporary = null
        stream = null
        entryCount = 0
    }

    private fun entry(
        name: String,
        bytes: ByteArray,
        stored: Boolean,
    ) {

        if (entryCount == MAX_ENTRIES) {
            throw JarEntryLimitExceeded()
        }

        val jarEntry = JarEntry(name).apply { timeLocal = ENTRY_TIME }
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
        entryCount++
    }

    private fun open(): JarOutputStream {

        val directory = jar.toAbsolutePath().parent
        Files.createDirectories(directory)

        val staged = Files.createTempFile(directory, ".${jar.fileName}-", ".tmp")
        temporary = staged

        val jarStream = JarOutputStream(Files.newOutputStream(staged).buffered())
        stream = jarStream

        return jarStream
    }

    private companion object {
        const val MAX_ENTRIES = 65_534
        val ENTRY_TIME: LocalDateTime = LocalDateTime.of(1980, 2, 1, 0, 0)
    }
}

internal class JarEntryLimitExceeded : RuntimeException()
