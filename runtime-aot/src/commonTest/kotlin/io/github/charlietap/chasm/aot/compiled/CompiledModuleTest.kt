@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.aot.compiled

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.aot.compiledModule
import io.github.charlietap.chasm.aot.store
import io.github.charlietap.chasm.compiler.backend.jvm.JvmModule
import io.github.charlietap.chasm.compiler.backend.jvm.JvmOptions
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.exports
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.prepareFunction
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.value.ExecutionValue
import io.github.charlietap.chasm.embedding.value.NumberValue.I32
import kotlinx.coroutines.runBlocking
import java.io.InputStream
import java.net.URLClassLoader
import java.nio.file.Files
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream
import java.util.zip.CRC32
import java.util.zip.ZipEntry
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

class CompiledModuleTest {

    @Test
    fun `a compiled module instantiates again and again from its declarations alone`() = runBlocking<Unit> {
        val bytes = fixture("declarations")
        val compiled = generate(bytes).compiledModule()
        val interpreted = InterpretedFixture(bytes)
        val calls = listOf(
            "started" to emptyList(),
            "active_data" to emptyList(),
            "passive_data" to emptyList(),
            "call_direct" to listOf(I32(0), I32(5)),
            "call_direct" to listOf(I32(1), I32(5)),
            "call_passive" to listOf(I32(4)),
            "reference" to listOf(I32(3)),
            "struct" to listOf(I32(9)),
            "catch" to listOf(I32(5)),
        )

        val expected = calls.map { (name, arguments) -> interpreted.call(name, *arguments.toTypedArray<ExecutionValue>()) }

        repeat(2) {
            val store = store()
            val instance = instance(store, compiled).expect("instantiates")
            calls.forEachIndexed { index, (name, arguments) ->
                assertEquals(expected[index], invoke(instance, name, arguments).expect(name), name)
            }
        }
    }

    @Test
    fun `each instance reads the data resource, and a missing one fails instantiation`() = runBlocking<Unit> {
        val generated = generate(fixture("declarations"))
        val present = CountingLoader(generated, generated.resources.getValue(checkNotNull(generated.module.data)))
        val missing = CountingLoader(generated, null)

        repeat(2) {
            instance(store(), present.compiledModule()).expect("instantiates")
            val result = instance(store(), missing.compiledModule())
            assertIs<ChasmResult.Error<*>>(result)
            assertTrue("data.chasmdata is missing" in result.error.toString(), result.error.toString())
        }
        assertEquals(2, present.reads)
        assertEquals(2, missing.reads)
    }

    @Test
    fun `a truncated data resource fails instantiation as unavailable data`() = runBlocking<Unit> {
        val generated = generate(fixture("declarations"))
        val data = generated.resources.getValue(checkNotNull(generated.module.data))
        val truncated = CountingLoader(generated, data.copyOf(data.size - 1))

        val result = instance(store(), truncated.compiledModule())

        assertIs<ChasmResult.Error<*>>(result)
        assertTrue("DataUnavailable" in result.error.toString(), result.error.toString())
    }

    @Test
    fun `instances made at once from many threads share one module and read nothing for a module without data`() {
        val generated = generate(fixture("add"))
        assertEquals(null, generated.module.data)
        val loader = CountingLoader(generated, null)
        val compiled = loader.compiledModule()
        val start = CountDownLatch(1)
        val results = ConcurrentLinkedQueue<List<ExecutionValue>>()

        val threads = List(8) { thread ->
            Thread {
                start.await()
                val store = store()
                val instance = runBlocking { instance(store, compiled) }.expect("instantiates")
                results += invoke(instance, "add", listOf(I32(thread), I32(1))).expect("add")
            }.apply { start() }
        }
        start.countDown()
        threads.forEach(Thread::join)

        assertEquals((1..8).map { listOf(I32(it)) }.toSet(), results.toSet())
        assertEquals(0, loader.reads)
    }

    @Test
    fun `a prepared function runs compiled code each time it is called`() = runBlocking<Unit> {
        val compiled = generate(fixture("add")).compiledModule()
        val store = store()
        val instance = instance(store, compiled).expect("instantiates")
        val add = prepareFunction(instance, "add").expect("prepares")

        assertEquals(listOf(I32(5)), add(listOf(I32(2), I32(3))).expect("first call"))
        assertEquals(listOf(I32(-1)), add(listOf(I32(Int.MAX_VALUE), I32(Int.MIN_VALUE))).expect("second call"))
    }

    @Test
    fun `an instance's exports are refused by another store`() = runBlocking<Unit> {
        val compiled = generate(fixture("declarations")).compiledModule()
        val owner = store()
        val other = store()
        val instance = instance(owner, compiled).expect("instantiates")
        val imports = exports(instance).all.map { export -> Import("owner", export.name, export.value) }

        val imported = assertFailsWith<IllegalArgumentException> {
            instance(other, compiled, imports)
        }
        assertTrue("different store" in imported.message.orEmpty(), imported.message)
        assertEquals(listOf(I32(40)), invoke(instance, "started").expect("started"))
    }

    @Test
    fun `instances of one compiled module keep their own state`() = runBlocking<Unit> {
        val compiled = generate(fixture("declarations")).compiledModule()
        val store = store()

        val first = instance(store, compiled).expect("first instantiates")
        val second = instance(store, compiled).expect("second instantiates")

        assertEquals(listOf(I32(40)), invoke(first, "started").expect("first started"))
        assertEquals(listOf(I32(40)), invoke(second, "started").expect("second started"))
        assertEquals(listOf(I32(7)), invoke(second, "passive_data").expect("second passive"))
        assertEquals(listOf(I32(7)), invoke(first, "passive_data").expect("first passive"))
    }

    @Test
    fun `modules packaged together in a jar each find their own declarations`() = runBlocking<Unit> {
        val jar = Files.createTempFile("compiled", ".jar")
        val modules = listOf("add" to "chasm.aot.add", "declarations" to "chasm.aot.declarations").map { (fixture, packageName) ->
            generate(fixture(fixture), JvmOptions(packageName = packageName))
        }
        JarOutputStream(Files.newOutputStream(jar)).use { output ->
            modules.forEach { generated ->
                generated.classes.forEach { (name, bytes) -> output.entry("$name.class", bytes) }
                generated.resources.forEach { (path, bytes) -> output.entry(path, bytes) }
            }
        }
        URLClassLoader(arrayOf(jar.toUri().toURL()), JvmModule::class.java.classLoader).use { loader ->
            val (add, declarations) = modules.map { generated ->
                compiledModule(generated.module.moduleClass, loader).expect("loads")
            }
            val store = store()
            val addInstance = instance(store, add).expect("add instantiates")
            val declarationsInstance = instance(store, declarations).expect("declarations instantiates")

            assertEquals(listOf(I32(5)), invoke(addInstance, "add", listOf(I32(2), I32(3))).expect("add"))
            assertEquals(listOf(I32(40)), invoke(declarationsInstance, "started").expect("started"))
        }
        Files.delete(jar)
    }

    @Test
    fun `a module whose data is stored uncompressed in a jar reads it in place for each instance`() = runBlocking<Unit> {
        val bytes = fixture("declarations")
        val interpreted = InterpretedFixture(bytes)
        val generated = generate(bytes, JvmOptions(packageName = "chasm.aot.stored"))
        val calls = listOf("active_data", "passive_data", "started")
        val expected = calls.map { name -> interpreted.call(name) }

        withJar(generated, stored = true) { compiled ->
            repeat(2) {
                val store = store()
                val instance = instance(store, compiled).expect("instantiates")
                calls.forEachIndexed { index, name ->
                    assertEquals(expected[index], invoke(instance, name).expect(name), name)
                }
            }
        }
    }

    @Test
    fun `a truncated data resource stored uncompressed fails instantiation as unavailable data`() = runBlocking<Unit> {
        val generated = generate(fixture("declarations"), JvmOptions(packageName = "chasm.aot.truncated"))
        val data = checkNotNull(generated.module.data)
        generated.resources[data] = generated.resources.getValue(data).copyOf(1)

        withJar(generated, stored = true) { compiled ->
            val result = instance(store(), compiled)

            assertIs<ChasmResult.Error<*>>(result)
            assertTrue("DataUnavailable" in result.error.toString(), result.error.toString())
        }
    }

    private inline fun withJar(
        generated: Generated,
        stored: Boolean,
        block: (Module) -> Unit,
    ) {
        val jar = Files.createTempFile("compiled", ".jar")
        JarOutputStream(Files.newOutputStream(jar)).use { output ->
            generated.classes.forEach { (name, bytes) -> output.entry("$name.class", bytes) }
            generated.resources.forEach { (path, bytes) -> output.entry(path, bytes, stored) }
        }
        try {
            URLClassLoader(arrayOf(jar.toUri().toURL()), JvmModule::class.java.classLoader).use { loader ->
                block(compiledModule(generated.module.moduleClass, loader).expect("loads"))
            }
        } finally {
            Files.delete(jar)
        }
    }

    @Test
    fun `a module whose name section does not decode instantiates without names`() = runBlocking<Unit> {
        val bytes = fixture("add") + byteArrayOf(0x00, 0x06, 0x04, 0x6E, 0x61, 0x6D, 0x65, 0x00)
        val compiled = generate(bytes, JvmOptions(stacktraces = true)).compiledModule()
        val store = store()

        val instance = instance(store, compiled).expect("instantiates")

        assertEquals(listOf(I32(5)), invoke(instance, "add", listOf(I32(2), I32(3))).expect("add"))
    }

    @Test
    fun `a trap in an imported start function is named by the module that defines it`() = runBlocking<Unit> {
        val boom = generate(fixture("boom"), JvmOptions(packageName = "chasm.aot.boom", stacktraces = true)).compiledModule()
        val starting = generate(fixture("imported_start"), JvmOptions(packageName = "chasm.aot.starting", stacktraces = true)).compiledModule()
        val store = store()
        val exporter = instance(store, boom).expect("boom instantiates")
        val imports = exports(exporter).all.map { export -> Import("boom", export.name, export.value) }

        val result = instance(store, starting, imports)

        assertIs<ChasmResult.Error<*>>(result)
        val trapped = assertIs<ExecutionError.Trapped>(result.error)
        val frames = checkNotNull(trapped.trap.stackTrace).frames
        assertEquals(listOf(Triple("boom", "explode", 0)), frames.map { frame -> Triple(frame.moduleName, frame.functionName, frame.functionIndex) })
        assertTrue(frames.all { frame -> frame.wasmOffset == null })
    }

    @Test
    fun `a trap in a compiled start function names every frame`() = runBlocking<Unit> {
        val compiled = generate(fixture("start_trap"), JvmOptions(stacktraces = true)).compiledModule()

        val result = instance(store(), compiled)

        assertIs<ChasmResult.Error<*>>(result)
        val trapped = assertIs<ExecutionError.Trapped>(result.error)
        val frames = checkNotNull(trapped.trap.stackTrace).frames
        assertEquals(listOf("boom" to 0, "start" to 1), frames.map { frame -> frame.functionName to frame.functionIndex })
        assertTrue(frames.all { frame -> frame.wasmOffset == null })
    }

    private fun JarOutputStream.entry(
        path: String,
        bytes: ByteArray,
        stored: Boolean = false,
    ) {
        putNextEntry(
            JarEntry(path).apply {
                if (stored) {
                    method = ZipEntry.STORED
                    size = bytes.size.toLong()
                    crc = CRC32().apply { update(bytes) }.value
                }
            },
        )
        write(bytes)
        closeEntry()
    }

    private class CountingLoader(
        generated: Generated,
        private val data: ByteArray?,
    ) : ClassLoader(JvmModule::class.java.classLoader) {
        private val classes = GeneratedClassLoader(generated)
        private val moduleClass = generated.module.moduleClass
        private val counter = AtomicInteger()
        val reads: Int get() = counter.get()

        fun compiledModule(): Module = compiledModule(loadClass(moduleClass)).expect("loads")

        override fun findClass(
            name: String,
        ): Class<*> {
            val bytes = classes.bytes(name) ?: throw ClassNotFoundException(name)
            return defineClass(name, bytes, 0, bytes.size)
        }

        override fun getResourceAsStream(
            name: String,
        ): InputStream? {
            counter.incrementAndGet()
            return data?.inputStream()
        }
    }
}
