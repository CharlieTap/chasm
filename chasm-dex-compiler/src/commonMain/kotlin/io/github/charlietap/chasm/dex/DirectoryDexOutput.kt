package io.github.charlietap.chasm.dex

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

internal class DirectoryDexOutput(
    private val root: Path,
) : AtomicDexOutput {

    private val created = ArrayList<Path>()
    private var checked = false

    override fun dexFile(
        name: String,
        bytes: ByteArray,
    ) {
        write(root.resolve(name), bytes)
    }

    override fun resource(
        path: String,
        bytes: ByteArray,
    ) {
        write(root.resolve(path), bytes)
    }

    override fun commit() {
        created.clear()
    }

    override fun discard() {

        created.asReversed().forEach { path ->
            val _ = runCatching { Files.deleteIfExists(path) }
        }

        created.clear()
    }

    private fun write(
        file: Path,
        bytes: ByteArray,
    ) {

        if (!checked) {
            requireEmpty()
            checked = true
        }

        createDirectories(file.toAbsolutePath().parent)
        Files.newOutputStream(file, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE).use { stream ->
            created.add(file)
            if (!file.toFile().setReadOnly()) {
                throw IOException("$file cannot be made read-only")
            }
            stream.write(bytes)
        }
    }

    private fun createDirectories(directory: Path) {

        val missing = generateSequence(directory) { path -> path.parent }
            .takeWhile { path -> !Files.isDirectory(path) }
            .toList()

        missing.asReversed().forEach { path ->
            Files.createDirectory(path)
            created.add(path)
        }
    }

    private fun requireEmpty() {

        if (!Files.isDirectory(root)) {
            return
        }

        val occupied = Files.newDirectoryStream(root).use { entries -> entries.iterator().hasNext() }
        if (occupied) {
            throw IOException("$root is not empty: compile each module into a new directory, see DexOutput.directory")
        }
    }
}
