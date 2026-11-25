package io.github.charlietap.chasm.compiled

import io.github.charlietap.chasm.compiled.internal.ClassLoaderResources
import java.io.InputStream
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
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ClassLoaderResourcesTest {

    private val bytes = ByteArray(4096) { index -> (index * 31).toByte() }
    private val directory: Path = Files.createTempDirectory("compiled-resources")

    @AfterTest
    fun cleanUp() {
        directory.toFile().deleteRecursively()
    }

    @Test
    fun `a resource stored uncompressed in a jar is read in place, in any order and into arrays`() {
        ClassLoaderResources(jar(stored = true)).data(PATH).open().use { reader ->
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
        ClassLoaderResources(jar(stored = false)).data(PATH).open().use { reader ->
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

        val loader = URLClassLoader(arrayOf(root.toUri().toURL()), ClassLoader.getPlatformClassLoader())

        ClassLoaderResources(loader).data(PATH).open().use { reader ->
            assertEquals(bytes.size.toLong(), reader.length)
            assertContentEquals(bytes.copyOfRange(100, 200), reader.read(100, 100))
        }
    }

    @Test
    fun `a jar whose comment holds an end record's signature is still read in place`() {
        val loader = jar(stored = true, comment = "PK\u0005\u0006 not an end record")

        ClassLoaderResources(loader).data(PATH).open().use { reader ->
            assertEquals(bytes.size.toLong(), reader.length)
            assertContentEquals(bytes.copyOfRange(0, 64), reader.read(0, 64))
        }
    }

    @Test
    fun `an entry whose name is percent-encoded in its URL is read in place`() {
        val path = "test/data dir/data.chasmdata"

        ClassLoaderResources(jar(stored = true, path = path)).data(path).open().use { reader ->
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

        val loader = URLClassLoader(arrayOf(jar.toUri().toURL()), ClassLoader.getPlatformClassLoader())

        ClassLoaderResources(loader).data(PATH).open().use { reader ->
            assertNull(reader.length)
        }
    }

    @Test
    fun `opening a missing resource fails`() {
        assertFailsWith<IllegalStateException> { ClassLoaderResources(jar(stored = true)).data("missing/data.chasmdata").open() }
    }

    @Test
    fun `the length of a resource is known whether it is mapped or streamed`() {
        assertEquals(bytes.size.toLong(), ClassLoaderResources(jar(stored = true)).length(PATH))
        assertEquals(bytes.size.toLong(), ClassLoaderResources(jar(stored = false)).length(PATH))
        assertNull(ClassLoaderResources(jar(stored = true)).length("missing/data.chasmdata"))
    }

    @Test
    fun `the length of a compressed resource comes from its entry without reading it`() {

        val loader = CountingStreams(jarFile(stored = false))

        assertEquals(bytes.size.toLong(), ClassLoaderResources(loader).length(PATH))
        assertEquals(0, loader.streams)
    }

    @Test
    fun `the data of a resource reuses the source its length was measured from`() {

        val resources = ClassLoaderResources(jar(stored = true))

        resources.length(PATH)

        assertSame(resources.data(PATH), resources.data(PATH))
    }

    @Test
    fun `a resource is found without being read`() {

        val loader = CountingStreams(jarFile(stored = false))
        val resources = ClassLoaderResources(loader)

        assertTrue(resources.contains(PATH))
        assertEquals(0, loader.streams)
        assertFalse(resources.contains("missing/data.chasmdata"))
    }

    private class CountingStreams(
        jar: Path,
    ) : URLClassLoader(arrayOf(jar.toUri().toURL()), getPlatformClassLoader()) {

        var streams = 0

        override fun getResourceAsStream(name: String): InputStream? {

            streams++

            return super.getResourceAsStream(name)
        }
    }

    private fun jar(
        stored: Boolean,
        path: String = PATH,
        comment: String? = null,
    ): ClassLoader {
        val jar = jarFile(stored, path, comment)
        return URLClassLoader(arrayOf(jar.toUri().toURL()), ClassLoader.getPlatformClassLoader())
    }

    private fun jarFile(
        stored: Boolean,
        path: String = PATH,
        comment: String? = null,
    ): Path {
        val jar = Files.createTempFile(directory, "data", ".jar")
        JarOutputStream(Files.newOutputStream(jar)).use { output ->
            comment?.let(output::setComment)
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

    private companion object {
        const val PATH = "test/data/data.chasmdata"
    }
}
