package io.github.charlietap.chasm.aot

import java.io.ByteArrayOutputStream
import java.io.FilterOutputStream
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.time.LocalDateTime
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream

/** Stages one compilation's output: [commit] publishes it, [discard] drops it, so a failed compilation writes nothing. */
internal interface OutputSink {

    fun classFile(
        internalName: String,
        bytes: ByteArray,
    )

    fun resource(
        path: String,
        write: (OutputStream) -> Unit,
    )

    /** Publishes the output, returning the files it wrote. */
    fun commit(): List<Path>

    fun discard()
}

internal fun CompileOutput.sink(): OutputSink = when (this) {
    is CompileOutput.Memory -> MemorySink(this)
    is CompileOutput.Directory -> DirectorySink(path)
    is CompileOutput.Jar -> JarSink(path)
}

private class MemorySink(
    private val memory: CompileOutput.Memory,
) : OutputSink {

    private val classes = LinkedHashMap<String, ByteArray>()
    private val resources = LinkedHashMap<String, ByteArray>()

    override fun classFile(
        internalName: String,
        bytes: ByteArray,
    ) {
        classes[internalName] = bytes
    }

    override fun resource(
        path: String,
        write: (OutputStream) -> Unit,
    ) {
        resources[path] = ByteArrayOutputStream().also(write).toByteArray()
    }

    override fun commit(): List<Path> {
        memory.classFiles.putAll(classes)
        memory.resourceFiles.putAll(resources)
        return emptyList()
    }

    override fun discard() = Unit
}

/** Writes each file beside its final path under a temporary name, renamed into place on commit. */
private class DirectorySink(
    private val root: Path,
) : OutputSink {

    private val staged = LinkedHashMap<Path, Path>()

    override fun classFile(
        internalName: String,
        bytes: ByteArray,
    ) {
        Files.write(stage("$internalName.class"), bytes)
    }

    override fun resource(
        path: String,
        write: (OutputStream) -> Unit,
    ) {
        Files.newOutputStream(stage(path)).use(write)
    }

    override fun commit(): List<Path> = staged.map { (file, temporary) ->
        Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING)
    }

    override fun discard() {
        staged.values.forEach { temporary -> runCatching { Files.deleteIfExists(temporary) } }
    }

    private fun stage(
        path: String,
    ): Path {
        val file = root.resolve(path)
        Files.createDirectories(file.parent)
        val temporary = Files.createTempFile(file.parent, ".${file.fileName}-", ".tmp")
        staged[file] = temporary
        return temporary
    }
}

/** Streams into temporary shards beside the jar, which replace it and its old shards on commit. */
private class JarSink(
    private val first: Path,
) : OutputSink {

    private val directory: Path = first.toAbsolutePath().parent
    private val baseName = first.fileName.toString().removeSuffix(".jar")
    private val temporary = ArrayList<Path>()
    private var jar: JarOutputStream? = null
    private var entries = 0

    override fun classFile(
        internalName: String,
        bytes: ByteArray,
    ) = entry("$internalName.class") { stream -> stream.write(bytes) }

    override fun resource(
        path: String,
        write: (OutputStream) -> Unit,
    ) = entry(path, write)

    /** Moves each shard over the old one first, so no jar goes missing while its replacement waits. */
    override fun commit(): List<Path> {
        if (jar == null && temporary.isEmpty()) open()
        jar?.close()
        jar = null
        val shards = temporary.mapIndexed { index, staged ->
            Files.move(staged, shard(index), StandardCopyOption.REPLACE_EXISTING)
        }
        removeShardsAfter(shards.size)
        return shards
    }

    override fun discard() {
        runCatching { jar?.close() }
        jar = null
        temporary.forEach { staged -> runCatching { Files.deleteIfExists(staged) } }
    }

    private inline fun entry(
        name: String,
        write: (OutputStream) -> Unit,
    ) {
        val stream = jar?.takeIf { entries < ENTRIES_PER_JAR } ?: open()
        stream.putNextEntry(JarEntry(name).apply { timeLocal = ENTRY_TIME })
        write(KeepOpen(stream))
        stream.closeEntry()
        entries++
    }

    private fun open(): JarOutputStream {
        jar?.close()
        Files.createDirectories(directory)
        val staged = Files.createTempFile(directory, ".$baseName-", ".jar.tmp")
        temporary.add(staged)
        entries = 0
        return JarOutputStream(Files.newOutputStream(staged).buffered()).also { jar = it }
    }

    private fun shard(
        index: Int,
    ): Path = if (index == 0) first else first.resolveSibling("$baseName-${index + 1}.jar")

    /** Removes the shards an earlier, larger compilation left, which would otherwise stay on the classpath. */
    private fun removeShardsAfter(
        count: Int,
    ) {
        val shard = Regex(Regex.escape(baseName) + "-(\\d+)\\.jar")
        Files.list(directory).use { files ->
            files.filter { file ->
                val number = shard.matchEntire(file.fileName.toString())?.groupValues?.get(1)?.toIntOrNull()
                number != null && number > count
            }.forEach(Files::deleteIfExists)
        }
    }

    /** Lets a resource writer close its stream without closing the jar. */
    private class KeepOpen(
        stream: OutputStream,
    ) : FilterOutputStream(stream) {

        override fun write(
            b: ByteArray,
            off: Int,
            len: Int,
        ) = out.write(b, off, len)

        override fun close() = flush()
    }

    private companion object {
        // Below the 65,535 entries past which a jar needs Zip64, which Android's build cannot read.
        const val ENTRIES_PER_JAR = 60_000

        // Fixed and local, so a module compiles to the same bytes in every timezone.
        val ENTRY_TIME: LocalDateTime = LocalDateTime.of(1980, 2, 1, 0, 0)
    }
}
