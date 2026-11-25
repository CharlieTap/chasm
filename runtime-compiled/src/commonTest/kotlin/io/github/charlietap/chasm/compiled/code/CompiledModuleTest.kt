package io.github.charlietap.chasm.compiled.code

import io.github.charlietap.chasm.compiled.CompiledModule
import io.github.charlietap.chasm.compiled.format.LineRange
import io.github.charlietap.chasm.compiled.format.dataResource
import io.github.charlietap.chasm.compiled.format.trapLinesKey
import io.github.charlietap.chasm.compiled.format.trapLinesResource
import io.github.charlietap.chasm.compiled.format.writeTrapLines
import io.github.charlietap.chasm.compiled.internal.CompiledModulePayload
import io.github.charlietap.chasm.compiled.module
import io.github.charlietap.chasm.compiled.store
import io.github.charlietap.chasm.compiler.backend.jvm.JvmModule
import io.github.charlietap.chasm.compiler.backend.jvm.JvmOptions
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.error.WasmFrame
import io.github.charlietap.chasm.embedding.exports
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.prepareFunction
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes._payload
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.value.ExecutionValue
import io.github.charlietap.chasm.embedding.value.NumberValue.I32
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream
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
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import io.github.charlietap.chasm.embedding.store as interpreterStore

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
            val store = store(compiled)
            val instance = instance(store, compiled).expect("instantiates")
            calls.forEachIndexed { index, (name, arguments) ->
                assertEquals(expected[index], invoke(instance, name, arguments).expect(name), name)
            }
        }
    }

    @Test
    fun `each instance reads the data resource, and a missing one fails at module()`() = runBlocking<Unit> {
        val generated = generate(fixture("declarations"))
        val present = CountingLoader(generated, generated.resources.getValue(generated.dataPath()))
        val missing = CountingLoader(generated, null)

        val presentModule = present.compiledModule()
        repeat(2) {
            instance(store(presentModule), presentModule).expect("instantiates")
        }
        val result = missing.loaded()

        assertEquals(3, present.reads)
        assertIs<ChasmResult.Error<*>>(result)
        assertTrue("WasmModule.chasmdata is missing" in result.error.toString(), result.error.toString())
        assertEquals(1, missing.reads)
    }

    @Test
    fun `a truncated data resource fails at module()`() {
        val generated = generate(fixture("declarations"))
        val data = generated.resources.getValue(generated.dataPath())

        val result = CountingLoader(generated, data.copyOf(data.size - 1)).loaded()

        val malformed = assertIs<ModuleError.Malformed>(assertIs<ChasmResult.Error<ModuleError>>(result).error)
        assertTrue("holds ${data.size - 1} bytes" in malformed.message, malformed.message)
    }

    @Test
    fun `instances made at once from many threads share one module and read nothing for a module without data`() {
        val generated = generate(fixture("add"))
        assertTrue(generated.resources.keys.none { path -> path.endsWith(".chasmdata") })
        val loader = CountingLoader(generated, null)
        val compiled = loader.compiledModule()
        val start = CountDownLatch(1)
        val results = ConcurrentLinkedQueue<List<ExecutionValue>>()

        val threads = List(8) { thread ->
            Thread {
                start.await()
                val store = store(compiled)
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
        val store = store(compiled)
        val instance = instance(store, compiled).expect("instantiates")
        val add = prepareFunction(instance, "add").expect("prepares")

        assertEquals(listOf(I32(5)), add(listOf(I32(2), I32(3))).expect("first call"))
        assertEquals(listOf(I32(-1)), add(listOf(I32(Int.MAX_VALUE), I32(Int.MIN_VALUE))).expect("second call"))
    }

    @Test
    fun `an instance's exports are refused by another store`() = runBlocking<Unit> {
        val compiled = generate(fixture("declarations")).compiledModule()
        val owner = store(compiled)
        val other = store(compiled)
        val instance = instance(owner, compiled).expect("instantiates")
        val imports = exports(instance).all.map { export -> Import("owner", export.name, export.value) }

        val imported = assertFailsWith<IllegalArgumentException> {
            instance(other, compiled, imports)
        }
        assertEquals("the import owner.${imports.first().entityName} belongs to a different store", imported.message)
        assertEquals(listOf(I32(40)), invoke(instance, "started").expect("started"))
    }

    @Test
    fun `instances of one compiled module keep their own state`() = runBlocking<Unit> {
        val compiled = generate(fixture("declarations")).compiledModule()
        val store = store(compiled)

        val first = instance(store, compiled).expect("first instantiates")
        val second = instance(store, compiled).expect("second instantiates")

        assertEquals(listOf(I32(40)), invoke(first, "started").expect("first started"))
        assertEquals(listOf(I32(40)), invoke(second, "started").expect("second started"))
        assertEquals(listOf(I32(7)), invoke(second, "passive_data").expect("second passive"))
        assertEquals(listOf(I32(7)), invoke(first, "passive_data").expect("first passive"))
    }

    @Test
    fun `modules packaged together in a jar each find their own declarations`() = runBlocking<Unit> {

        val entryClasses = listOf("add" to "chasm.aot.add.WasmModule", "declarations" to "chasm.aot.declarations.WasmModule")
        val modules = entryClasses.map { (fixture, entryClass) ->
            generate(fixture(fixture), JvmOptions(entryClass = entryClass))
        }

        withJar(modules, stored = false) { (add, declarations) ->
            val store = store(add)
            val addInstance = instance(store, add).expect("add instantiates")
            val declarationsInstance = instance(store, declarations).expect("declarations instantiates")

            assertEquals(listOf(I32(5)), invoke(addInstance, "add", listOf(I32(2), I32(3))).expect("add"))
            assertEquals(listOf(I32(40)), invoke(declarationsInstance, "started").expect("started"))
        }
    }

    @Test
    fun `a trap in one of two modules sharing a package names that module's frames`() {

        val expected = runBlocking {
            val boom = module(fixture(BOOM)).expect("boom decodes")
            val caller = module(fixture(CALLER)).expect("caller decodes")
            sharedPackageTraps(interpreterStore(DIAGNOSTICS), boom, caller)
        }
        val modules = listOf(BOOM to "chasm.aot.shared.Boom", CALLER to "chasm.aot.shared.Caller").map { (fixture, entryClass) ->
            generate(fixture(fixture), JvmOptions(entryClass = entryClass, runtime = DIAGNOSTICS))
        }

        withJar(modules, stored = false) { (boom, caller) ->

            val actual = runBlocking { sharedPackageTraps(store(boom), boom, caller) }

            assertEquals(listOf("explode"), expected.first.map { frame -> frame.functionName })
            assertEquals(listOf("explode", "call_boom", "call_owner"), expected.second.map { frame -> frame.functionName })
            assertEquals(expected.first.map(::named), actual.first.map(::named))
            assertEquals(expected.second.map(::named), actual.second.map(::named))
        }
    }

    @Test
    fun `the post-shrink line table is read from the class path when assets hold none`() {

        val generated = generate(fixture(BOOM), JvmOptions(entryClass = "chasm.aot.lines.Module", runtime = DIAGNOSTICS))
        generated.resources[trapLinesResource("chasm/aot/lines/Module")] = linesTable(original = 7)

        val trapLines = (generated.compiledModule()._payload() as CompiledModulePayload).trapLines

        assertEquals(7, checkNotNull(trapLines).original("Module\$Code0", "f0", 1))
    }

    @Test
    fun `the post-shrink line table in assets is read before one on the class path`() {

        val generated = generate(fixture(BOOM), JvmOptions(entryClass = "chasm.aot.lines.Module", runtime = DIAGNOSTICS))
        generated.resources[trapLinesResource("chasm/aot/lines/Module")] = linesTable(original = 7)
        generated.resources["assets/" + trapLinesResource("chasm/aot/lines/Module")] = linesTable(original = 9)

        val trapLines = (generated.compiledModule()._payload() as CompiledModulePayload).trapLines

        assertEquals(9, checkNotNull(trapLines).original("Module\$Code0", "f0", 1))
    }

    private fun linesTable(original: Int): ByteArray {

        val table = ByteArrayOutputStream()
        val range = LineRange(first = 1, last = 1, original = original, linear = false)
        writeTrapLines(table, mapOf(trapLinesKey("Module\$Code0", "f0") to listOf(range)))

        return table.toByteArray()
    }

    private suspend fun sharedPackageTraps(
        store: Store,
        boom: Module,
        caller: Module,
    ): Pair<List<WasmFrame>, List<WasmFrame>> {

        val boomInstance = instance(store, boom).expect("boom instantiates")
        val explode = assertNotNull(exports(boomInstance).function("boom"))
        val callerInstance = instance(store, caller, listOf(Import("owner", "boom", explode))).expect("caller instantiates")

        val direct = assertIs<ExecutionError.Trapped>(assertIs<ChasmResult.Error<*>>(invoke(boomInstance, "boom")).error)
        val called = assertIs<ExecutionError.Trapped>(assertIs<ChasmResult.Error<*>>(invoke(callerInstance, "call_owner")).error)

        return checkNotNull(direct.trap.stackTrace).frames to checkNotNull(called.trap.stackTrace).frames
    }

    private fun named(frame: WasmFrame): Triple<String?, String?, Int> {
        return Triple(frame.moduleName, frame.functionName, frame.functionIndex)
    }

    @Test
    fun `a module whose data is stored uncompressed in a jar reads it in place for each instance`() = runBlocking<Unit> {
        val bytes = fixture("declarations")
        val interpreted = InterpretedFixture(bytes)
        val generated = generate(bytes, JvmOptions(entryClass = "chasm.aot.stored.WasmModule"))
        val calls = listOf("active_data", "passive_data", "started")
        val expected = calls.map { name -> interpreted.call(name) }

        withJar(listOf(generated), stored = true) { (compiled) ->
            repeat(2) {
                val store = store(compiled)
                val instance = instance(store, compiled).expect("instantiates")
                calls.forEachIndexed { index, name ->
                    assertEquals(expected[index], invoke(instance, name).expect(name), name)
                }
            }
        }
    }

    @Test
    fun `a truncated data resource stored uncompressed fails at module()`() {
        val generated = generate(fixture("declarations"), JvmOptions(entryClass = "chasm.aot.truncated.WasmModule"))
        val data = generated.dataPath()
        generated.resources[data] = generated.resources.getValue(data).copyOf(1)

        withJarLoader(listOf(generated), stored = true) { loader ->
            val compiled = loader.loadClass(generated.module.moduleClass).getField("COMPILED").get(null) as CompiledModule

            val result = module(compiled)

            val malformed = assertIs<ModuleError.Malformed>(assertIs<ChasmResult.Error<ModuleError>>(result).error)
            assertTrue("holds 1 bytes" in malformed.message, malformed.message)
        }
    }

    private inline fun withJar(
        modules: List<Generated>,
        stored: Boolean,
        block: (List<Module>) -> Unit,
    ) {
        withJarLoader(modules, stored) { loader ->
            block(
                modules.map { generated ->
                    module(loader.loadClass(generated.module.moduleClass).getField("COMPILED").get(null) as CompiledModule).expect("loads")
                },
            )
        }
    }

    private inline fun withJarLoader(
        modules: List<Generated>,
        stored: Boolean,
        block: (ClassLoader) -> Unit,
    ) {
        val jar = Files.createTempFile("compiled", ".jar")
        JarOutputStream(Files.newOutputStream(jar)).use { output ->
            modules.forEach { generated ->
                generated.classes.forEach { (name, bytes) -> output.entry("$name.class", bytes) }
                generated.resources.forEach { (path, bytes) -> output.entry(path, bytes, stored) }
            }
        }
        try {
            URLClassLoader(arrayOf(jar.toUri().toURL()), JvmModule::class.java.classLoader).use { loader ->
                block(loader)
            }
        } finally {
            Files.delete(jar)
        }
    }

    private fun Generated.dataPath(): String {
        return dataResource(module.moduleClass.replace('.', '/'))
    }

    @Test
    fun `a module whose name section does not decode instantiates without names`() = runBlocking<Unit> {
        val bytes = fixture("add") + byteArrayOf(0x00, 0x06, 0x04, 0x6E, 0x61, 0x6D, 0x65, 0x00)
        val compiled = generate(bytes, JvmOptions(runtime = DIAGNOSTICS)).compiledModule()
        val store = store(compiled)

        val instance = instance(store, compiled).expect("instantiates")

        assertEquals(listOf(I32(5)), invoke(instance, "add", listOf(I32(2), I32(3))).expect("add"))
    }

    @Test
    fun `a trap in an imported start function is named by the module that defines it`() = runBlocking<Unit> {
        val boom = generate(fixture("boom"), JvmOptions(entryClass = "chasm.aot.boom.WasmModule", runtime = DIAGNOSTICS)).compiledModule()
        val starting = generate(fixture("imported_start"), JvmOptions(entryClass = "chasm.aot.starting.WasmModule", runtime = DIAGNOSTICS))
            .compiledModule()
        val store = store(boom)
        val exporter = instance(store, boom).expect("boom instantiates")
        val imports = exports(exporter).all.map { export -> Import("boom", export.name, export.value) }

        val result = instance(store, starting, imports)

        assertIs<ChasmResult.Error<*>>(result)
        val trapped = assertIs<ExecutionError.Trapped>(result.error)
        val frames = checkNotNull(trapped.trap.stackTrace).frames
        val named = frames.map { frame -> Triple(frame.moduleName, frame.functionName, frame.functionIndex) }
        assertEquals(listOf(Triple("boom", "explode", 0)), named)
        assertTrue(frames.all { frame -> frame.wasmOffset == null })
    }

    @Test
    fun `a trap in a compiled start function names every frame`() = runBlocking<Unit> {
        val compiled = generate(fixture("start_trap"), JvmOptions(runtime = DIAGNOSTICS)).compiledModule()

        val result = instance(store(compiled), compiled)

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

        fun compiledModule(): Module {
            return loaded().expect("loads")
        }

        fun loaded(): ChasmResult<Module, ModuleError> {
            return module(loadClass(moduleClass).getField("COMPILED").get(null) as CompiledModule)
        }

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

    private companion object {
        const val BOOM = "boom"
        const val CALLER = "trap_diagnostics_mixed"
        val DIAGNOSTICS = RuntimeConfig(stacktraces = true)
    }
}
