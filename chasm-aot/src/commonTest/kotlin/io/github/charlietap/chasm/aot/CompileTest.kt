package io.github.charlietap.chasm.aot

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.aot.CompileError.Stage
import io.github.charlietap.chasm.aot.internal.COMPILED_RUNTIME_ABI
import io.github.charlietap.chasm.aot.internal.readTrapSites
import io.github.charlietap.chasm.aot.internal.trapSitesResource
import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.GCThreshold
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.config.StoreLimits
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Path
import java.util.jar.JarFile
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertSame
import kotlin.test.assertTrue

private val EMPTY_MODULE = byteArrayOf(0x00, 0x61, 0x73, 0x6d, 0x01, 0x00, 0x00, 0x00)

@OptIn(ExperimentalChasmApi::class, InternalChasmApi::class)
class CompileTest {

    @Test
    fun `a compiled module names its class and the data resource it wrote`() {
        val output = CompileOutput.Memory()

        val artifact = compile(module("declarations"), output, CompileConfig("test.compiled.declarations")).expect("compiles")

        val data = checkNotNull(artifact.dataResourcePath)
        assertEquals("test.compiled.declarations.WasmModule", artifact.moduleClassName)
        assertEquals("test/compiled/declarations/data.chasmdata", data)
        assertTrue(data.endsWith(CompiledModuleArtifact.DATA_RESOURCE_EXTENSION))
        assertTrue("test/compiled/declarations/WasmModule" in output.classes)
        assertTrue(output.resources.getValue(data).isNotEmpty())
        assertEquals(emptyList(), artifact.files)
    }

    @Test
    fun `a module without data segments writes no data resource`() {
        val output = CompileOutput.Memory()

        val artifact = compile(EMPTY_MODULE, output, CompileConfig("test.empty")).expect("compiles")

        assertEquals(null, artifact.dataResourcePath)
        assertTrue(output.resources.keys.none { name -> name.endsWith(CompiledModuleArtifact.DATA_RESOURCE_EXTENSION) })
    }

    @Test
    fun `a module compiles to the same output from a file as from its bytes`() {
        val bytes = module("declarations")
        val file = Files.createTempFile("module", ".wasm").also { Files.write(it, bytes) }
        val fromFile = CompileOutput.Memory()
        val fromBytes = CompileOutput.Memory()

        compile(file, fromFile, CompileConfig("test.same")).expect("compiles from the file")
        compile(bytes, fromBytes, CompileConfig("test.same")).expect("compiles from the bytes")
        Files.delete(file)

        assertEquals(fromBytes.entries().keys, fromFile.entries().keys)
        fromBytes.entries().forEach { (name, contents) -> assertContentEquals(contents, fromFile.entries().getValue(name), name) }
    }

    @Test
    fun `an Android module reaches memory as Android's memories allow`() {
        val output = CompileOutput.Memory()
        val config = CompileConfig("test.android") { target(CompileTarget.ANDROID) }

        compile(module("declarations"), output, config).expect("compiles")

        assertTrue(output.classes.values.any { bytes -> "sun/misc/Unsafe" in String(bytes, Charsets.ISO_8859_1) })
    }

    @Test
    fun `methods may be as long as the target compiles to machine code unless set`() {
        assertEquals(8_000, CompileConfig("test.jvm").maxMethodBytes)
        assertEquals(12_000, CompileConfig("test.android") { target(CompileTarget.ANDROID) }.maxMethodBytes)
        val set = CompileConfig("test.set") { target(CompileTarget.ANDROID).maxMethodBytes(20_000).maxMethodBytes(function = 3, 9_000) }
        assertEquals(20_000, set.maxMethodBytes)
        assertEquals(mapOf(3 to 9_000), set.functionMaxMethodBytes)
    }

    @Test
    fun `failures say where compilation failed`() {
        val missing = Files.createTempDirectory("compile").resolve("missing.wasm")
        val blocked = Files.createTempFile("compile", ".blocked")
        val cases = listOf(
            compile(module("declarations"), CompileOutput.Memory(), CompileConfig("test.class.name")) to Stage.CONFIGURATION,
            compile(module("declarations"), CompileOutput.Memory(), CompileConfig("1test")) to Stage.CONFIGURATION,
            compile(module("declarations"), CompileOutput.Memory(), CompileConfig("test.small") { maxMethodBytes(999) }) to Stage.CONFIGURATION,
            compile(module("declarations"), CompileOutput.Memory(), CompileConfig("test.large") { maxMethodBytes(32_768) }) to Stage.CONFIGURATION,
            compile(module("declarations"), CompileOutput.Memory(), CompileConfig("test.fuel") { initialFuel(10) }) to Stage.CONFIGURATION,
            compile(
                module("declarations"),
                CompileOutput.Memory(),
                CompileConfig("test.negative") { store(StoreConfig(meterFuel = true)).initialFuel(-1) },
            ) to Stage.CONFIGURATION,
            compile(missing, CompileOutput.Memory(), CompileConfig("test.missing")) to Stage.INPUT,
            compile(byteArrayOf(1, 2, 3), CompileOutput.Memory(), CompileConfig("test.garbage")) to Stage.DECODE,
            compile(module("invalid"), CompileOutput.Memory(), CompileConfig("test.invalid")) to Stage.VALIDATION,
            compile(module("simd"), CompileOutput.Memory(), CompileConfig("test.simd")) to Stage.GENERATION,
            compile(module("declarations"), CompileOutput.Directory(blocked.resolve("classes")), CompileConfig("test.failing")) to Stage.OUTPUT,
        )
        Files.delete(blocked)

        cases.forEach { (result, stage) ->
            assertIs<CompileResult.Error>(result)
            assertEquals(stage, result.error.stage, result.error.toString())
        }
        val simd = cases.single { (_, stage) -> stage == Stage.GENERATION }.first as CompileResult.Error
        assertEquals(listOf(2), simd.error.functions, simd.error.toString())
        assertTrue("Simd" in simd.error.error, simd.error.toString())
    }

    @Test
    fun `the metadata records what the module was built with`() {
        val store = StoreConfig(
            meterFuel = true,
            interruptible = true,
            gcStrategy = GCStrategy.TRADITIONAL,
            gcThreshold = GCThreshold.MB(4),
            limits = StoreLimits(gcHeapBytes = 1_024, memoryPages = 16, tableElements = 32, trapOnGrowFailure = true, stackBytes = 65_536),
        )
        val config = CompileConfig("test.metadata") { store(store).initialFuel(1_000).stacktraces(true) }

        val artifact = compile(module("declarations"), CompileOutput.Memory(), config).expect("compiles")

        assertEquals(Capabilities(meterFuel = true, interruptible = true, stacktraces = true), artifact.metadata.capabilities)
        assertEquals(store, artifact.metadata.storeConfig)
        assertEquals(1_000L, artifact.metadata.initialFuel)
        assertEquals(COMPILED_RUNTIME_ABI, artifact.metadata.runtimeAbi)
    }

    @Test
    fun `a module reads back its metadata from memory, a directory and a jar`() {
        val store = StoreConfig(
            meterFuel = true,
            gcStrategy = GCStrategy.ARENA,
            gcThreshold = GCThreshold.KB(64),
            limits = StoreLimits(memoryPages = 8, trapOnGrowFailure = true, stackBytes = 32_768),
        )
        val config = CompileConfig("test.roundtrip") { store(store).initialFuel(77) }
        val directory = Files.createTempDirectory("compile")

        val memory = CompileOutput.Memory()
        val inMemory = memory.load(compile(module("declarations"), memory, config).expect("compiles into memory"))
        val classes = compile(module("declarations"), CompileOutput.Directory(directory.resolve("classes")), config).expect("compiles into a directory")
        val jar = compile(module("declarations"), CompileOutput.Jar(directory.resolve("module.jar")), config).expect("compiles into a jar")

        val modules = listOf(
            inMemory,
            loaded(directory.resolve("classes"), classes),
            loaded(jar.files.single(), jar),
        )
        modules.forEach { module ->
            assertEquals(Capabilities(meterFuel = true, interruptible = false, stacktraces = false), module.metadata.capabilities)
            assertEquals(store, module.metadata.storeConfig)
            assertEquals(77L, module.metadata.initialFuel)
        }
    }

    @Test
    fun `a module that starts above the store limits baked into it fails compilation`() {
        val memory = CompileConfig("test.memory.limit") { store(StoreConfig(limits = StoreLimits(memoryPages = 0))) }
        val table = CompileConfig("test.table.limit") { store(StoreConfig(limits = StoreLimits(tableElements = 1))) }
        val fits = CompileConfig("test.fits") { store(StoreConfig(limits = StoreLimits(memoryPages = 1, tableElements = 2))) }

        val memoryResult = compile(module("declarations"), CompileOutput.Memory(), memory)
        val tableResult = compile(module("declarations"), CompileOutput.Memory(), table)

        assertIs<CompileResult.Error>(memoryResult)
        assertEquals(CompileError("memory 0 starts at 1 pages, above the store's limit of 0 pages", Stage.CONFIGURATION), memoryResult.error)
        assertIs<CompileResult.Error>(tableResult)
        assertEquals(CompileError("table 0 starts at 2 elements, above the store's limit of 1 elements", Stage.CONFIGURATION), tableResult.error)
        assertIs<CompileResult.Success>(compile(module("declarations"), CompileOutput.Memory(), fits))
    }

    @Test
    fun `a directory output lists the files it wrote`() {
        val directory = Files.createTempDirectory("compile").resolve("classes")

        val artifact = compile(module("declarations"), CompileOutput.Directory(directory), CompileConfig("test.listed")).expect("compiles")

        assertTrue(directory.resolve("test/listed/WasmModule.class") in artifact.files)
        assertTrue(directory.resolve(checkNotNull(artifact.dataResourcePath)) in artifact.files)
        assertTrue(artifact.files.all(Files::exists))
    }

    @Test
    fun `a jar output replaces the jars an earlier compilation left`() {
        val directory = Files.createTempDirectory("compile")
        val first = directory.resolve("module.jar")
        Files.write(first, byteArrayOf(1))
        Files.write(directory.resolve("module-2.jar"), byteArrayOf(2))
        Files.write(directory.resolve("other-2.jar"), byteArrayOf(3))

        val artifact = compile(module("declarations"), CompileOutput.Jar(first), CompileConfig("test.jar")).expect("compiles")

        assertEquals(listOf(first), artifact.files)
        assertFalse(Files.exists(directory.resolve("module-2.jar")))
        assertTrue(Files.exists(directory.resolve("other-2.jar")))
        JarFile(first.toFile()).use { jar -> assertTrue(jar.getEntry("test/jar/WasmModule.class") != null) }
        assertEquals(setOf("module.jar", "other-2.jar"), Files.list(directory).use { files -> files.map { it.fileName.toString() }.toList().toSet() })
    }

    @Test
    fun `a jar compiles to the same bytes every time`() {
        val directory = Files.createTempDirectory("compile")

        compile(module("declarations"), CompileOutput.Jar(directory.resolve("a.jar")), CompileConfig("test.same.jar")).expect("compiles")
        compile(module("declarations"), CompileOutput.Jar(directory.resolve("b.jar")), CompileConfig("test.same.jar")).expect("compiles")

        assertContentEquals(Files.readAllBytes(directory.resolve("a.jar")), Files.readAllBytes(directory.resolve("b.jar")))
    }

    @Test
    fun `a failed compilation leaves earlier output alone`() {
        val directory = Files.createTempDirectory("compile")
        val jar = directory.resolve("module.jar")
        Files.write(jar, byteArrayOf(1, 2, 3))
        val memory = CompileOutput.Memory()

        assertIs<CompileResult.Error>(compile(module("simd"), CompileOutput.Jar(jar), CompileConfig("test.failed")))
        assertIs<CompileResult.Error>(compile(module("simd"), memory, CompileConfig("test.failed")))
        assertIs<CompileResult.Error>(compile(module("simd"), CompileOutput.Directory(directory.resolve("classes")), CompileConfig("test.failed")))

        assertContentEquals(byteArrayOf(1, 2, 3), Files.readAllBytes(jar))
        assertEquals(setOf("module.jar"), Files.list(directory).use { files -> files.map { it.fileName.toString() }.toList().toSet() })
        assertTrue(memory.classes.isEmpty())
    }

    @Test
    fun `stacktraces record the offset of every trap and call site`() {
        val output = CompileOutput.Memory()

        compile(module("trap_diagnostics"), output, CompileConfig("test.sites") { stacktraces(true) }).expect("compiles")

        val sites = checkNotNull(readTrapSites(output.resources.getValue(trapSitesResource("test/sites")).inputStream()))
        val expected = mapOf(
            1 to listOf(0x14f),
            2 to listOf(0x153),
            3 to listOf(0x158),
            4 to listOf(0x161),
            5 to listOf(0x16b),
            6 to listOf(0x176),
            7 to listOf(0x17e),
            8 to listOf(0x187),
            9 to listOf(0x191),
            10 to listOf(0x19d),
            11 to listOf(0x1a9),
            12 to listOf(0x1b0),
            13 to listOf(0x1b7),
            14 to listOf(0x1c2, 0x1c9),
            15 to listOf(0x1d0),
            16 to listOf(0x1d5),
            18 to listOf(0x1de),
            19 to listOf(0x1e8),
        )
        expected.forEach { (function, offsets) ->
            val table = checkNotNull(sites.function(function)) { "no sites for function $function" }
            val recorded = (0 until table.size).map(table::offset).toSet()
            offsets.forEach { offset -> assertTrue(offset in recorded, "function $function has no site at 0x${offset.toString(16)}") }
        }
    }

    @Test
    fun `an Android module records which inline access failed when stacktraces are on`() {
        val plain = CompileOutput.Memory()
        val diagnosed = CompileOutput.Memory()

        compile(module("trap_diagnostics"), plain, CompileConfig("test.android.plain") { target(CompileTarget.ANDROID) }).expect("compiles")
        val config = CompileConfig("test.android.sites") { target(CompileTarget.ANDROID).stacktraces(true) }
        compile(module("trap_diagnostics"), diagnosed, config).expect("compiles with stacktraces")

        fun CompileOutput.Memory.records() = classes.values.any { bytes -> "memoryAccessOutOfBounds" in String(bytes, Charsets.ISO_8859_1) }
        assertTrue(diagnosed.records())
        assertTrue(!plain.records())
    }

    @Test
    fun `a module compiled without stacktraces writes no trap sites and keeps only its module class`() {
        val output = CompileOutput.Memory()

        val artifact = compile(module("trap_diagnostics"), output, CompileConfig("test.plain")).expect("compiles")

        assertTrue(output.resources.keys.none { name -> name.endsWith(".chasmsites") })
        assertEquals("-keep class test.plain.WasmModule { public static *** INSTANCE; }\n", artifact.shrinkerRules)
    }

    @Test
    fun `stacktraces keep the compiled functions' names and line numbers through a shrinker`() {
        val config = CompileConfig("test.rules") { stacktraces(true) }

        val artifact = compile(module("trap_diagnostics"), CompileOutput.Memory(), config).expect("compiles")

        assertEquals(
            "-keep class test.rules.WasmModule { public static *** INSTANCE; }\n" +
                "-keepattributes LineNumberTable\n" +
                "-keep class test.rules.Code*, test.rules.Chunks* { static *** f*(...); }\n" +
                "-keep,allowobfuscation,allowshrinking class io.github.charlietap.chasm.executor.invoker.compiled.CompiledEntry " +
                "{ *** invoke(...); }\n",
            artifact.shrinkerRules,
        )
    }

    @Test
    fun `a module whose names do not decode is decoded again without them`() {
        val module = WasmModuleDecoder(ModuleConfig(), EMPTY_MODULE).getOrThrow { IllegalStateException(it.toString()) }
        val names = ArrayList<Boolean>()

        val decoded = decodeForCompilation(ModuleConfig()) { config ->
            names += config.decodeNameSection
            if (config.decodeNameSection) Err(CompileError("bad names", Stage.DECODE)) else Ok(module)
        }

        assertEquals(listOf(true, false), names)
        assertSame(module, decoded.get())
    }

    @Test
    fun `a binary that cannot be read is not read again`() {
        var reads = 0

        val decoded = decodeForCompilation(ModuleConfig()) {
            reads++
            Err(CompileError("cannot read", Stage.INPUT))
        }

        assertEquals(1, reads)
        assertEquals(Stage.INPUT, decoded.getError()?.stage)
    }

    private fun module(
        name: String,
    ): ByteArray =
        checkNotNull(CompileTest::class.java.getResourceAsStream("/modules/$name.wasm")) { "missing module $name" }.use { it.readBytes() }

    private fun CompileOutput.Memory.entries(): Map<String, ByteArray> =
        classes.mapKeys { (name, _) -> "$name.class" } + resources

    private fun loaded(
        path: Path,
        artifact: CompiledModuleArtifact,
    ): CompiledModule {
        val loader = URLClassLoader(arrayOf(path.toUri().toURL()), CompileTest::class.java.classLoader)
        return loader.loadClass(artifact.moduleClassName).getField("INSTANCE").get(null) as CompiledModule
    }
}
