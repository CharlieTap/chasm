package io.github.charlietap.chasm.runtime.compiled

import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Path
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream
import java.util.zip.CRC32
import java.util.zip.ZipEntry
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class CompiledResourcesTest {

    private val bytes = ByteArray(4096) { index -> (index * 31).toByte() }
    private val directory: Path = Files.createTempDirectory("compiled-resources")

    @AfterTest
    fun cleanUp() {
        directory.toFile().deleteRecursively()
    }

    @Test
    fun `a resource stored uncompressed in a jar is read in place, in any order and into arrays`() {
        dataSource(jar(stored = true), PATH).open().use { reader ->
            assertEquals(bytes.size.toLong(), reader.length)
            assertContentEquals(bytes.copyOfRange(1000, 1100), reader.read(1000, 100))
            assertContentEquals(bytes.copyOfRange(10, 20), reader.read(10, 10))

            val destination = ByteArray(40)
            reader.read(4000, 16, destination, 8)
            assertContentEquals(bytes.copyOfRange(4000, 4016), destination.copyOfRange(8, 24))
        }
    }

    @Test
    fun `a compressed resource is read as a stream, in order`() {
        dataSource(jar(stored = false), PATH).open().use { reader ->
            assertNull(reader.length)
            assertContentEquals(bytes.copyOfRange(10, 20), reader.read(10, 10))
            assertContentEquals(bytes.copyOfRange(1000, 1100), reader.read(1000, 100))
            assertFailsWith<IllegalArgumentException> { reader.read(0, 1) }
        }
    }

    @Test
    fun `a resource that is a file of its own is read in place`() {
        val root = directory.resolve("classes")
        Files.createDirectories(root.resolve(PATH).parent)
        Files.write(root.resolve(PATH), bytes)
        Files.createDirectories(root.resolve(MARKER_FILE).parent)
        Files.write(root.resolve(MARKER_FILE), markerBytes())

        dataSource(marker(URLClassLoader(arrayOf(root.toUri().toURL()), ClassLoader.getPlatformClassLoader())), PATH).open().use { reader ->
            assertEquals(bytes.size.toLong(), reader.length)
            assertContentEquals(bytes.copyOfRange(100, 200), reader.read(100, 100))
        }
    }

    @Test
    fun `a jar whose comment holds an end record's signature is still read in place`() {
        val owner = jar(stored = true, comment = "PK\u0005\u0006 not an end record")

        dataSource(owner, PATH).open().use { reader ->
            assertEquals(bytes.size.toLong(), reader.length)
            assertContentEquals(bytes.copyOfRange(0, 64), reader.read(0, 64))
        }
    }

    @Test
    fun `an entry whose name is percent-encoded in its URL is read in place`() {
        val path = "test/data dir/data.chasmdata"

        dataSource(jar(stored = true, path = path), path).open().use { reader ->
            assertEquals(bytes.size.toLong(), reader.length)
        }
    }

    @Test
    fun `an entry whose local header disagrees with the directory is read as a stream`() {
        val jar = jarFile(stored = true)
        val archive = Files.readAllBytes(jar)
        val header = localHeader(archive, PATH)
        archive[header + 18] = (archive[header + 18] + 1).toByte()
        Files.write(jar, archive)

        dataSource(marker(URLClassLoader(arrayOf(jar.toUri().toURL()), ClassLoader.getPlatformClassLoader())), PATH).open().use { reader ->
            assertNull(reader.length)
        }
    }

    @Test
    fun `opening a missing resource fails`() {
        assertFailsWith<IllegalStateException> { dataSource(jar(stored = true), "missing/data.chasmdata").open() }
    }

    private fun jar(
        stored: Boolean,
        path: String = PATH,
        comment: String? = null,
    ): Class<*> {
        val jar = jarFile(stored, path, comment)
        return marker(URLClassLoader(arrayOf(jar.toUri().toURL()), ClassLoader.getPlatformClassLoader()))
    }

    private fun jarFile(
        stored: Boolean,
        path: String = PATH,
        comment: String? = null,
    ): Path {
        val jar = Files.createTempFile(directory, "data", ".jar")
        JarOutputStream(Files.newOutputStream(jar)).use { output ->
            comment?.let(output::setComment)
            output.putNextEntry(JarEntry(MARKER_FILE))
            output.write(markerBytes())
            output.closeEntry()
            output.putNextEntry(
                JarEntry(path).apply {
                    if (stored) {
                        method = ZipEntry.STORED
                        size = bytes.size.toLong()
                        crc = CRC32().apply { update(bytes) }.value
                    }
                },
            )
            output.write(bytes)
            output.closeEntry()
        }
        return jar
    }

    private fun localHeader(
        archive: ByteArray,
        name: String,
    ): Int {
        val signature = byteArrayOf(0x50, 0x4b, 0x03, 0x04) + ByteArray(22) + byteArrayOf(name.length.toByte(), 0)
        return archive.indices.first { at ->
            at + 30 + name.length <= archive.size &&
                (0 until 4).all { index -> archive[at + index] == signature[index] } &&
                archive[at + 26] == signature[26] &&
                String(archive, at + 30, name.length) == name
        }
    }

    private fun marker(
        loader: ClassLoader,
    ): Class<*> = loader.loadClass(Marker::class.java.name).also { marker ->
        check(marker.classLoader === loader) { "the marker was not loaded from the test's class path" }
    }

    private fun markerBytes(): ByteArray = checkNotNull(Marker::class.java.getResourceAsStream("/$MARKER_FILE")).use { it.readBytes() }

    private companion object {
        const val PATH = "test/data/data.chasmdata"
        val MARKER_FILE = Marker::class.java.name.replace('.', '/') + ".class"
    }
}

class Marker
