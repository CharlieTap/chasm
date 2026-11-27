package io.github.charlietap.chasm.dex

import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.dexbacked.DexBackedClassDef
import com.android.tools.smali.dexlib2.dexbacked.DexBackedDexFile
import com.android.tools.smali.dexlib2.iface.instruction.WideLiteralInstruction
import com.github.michaelbull.result.getError
import io.github.charlietap.chasm.compiled.format.COMPILED_RUNTIME_ABI
import io.github.charlietap.chasm.compiled.format.UNPINNED_OFFSET
import io.github.charlietap.chasm.compiler.backend.dex.ParallelModuleLowerer
import io.github.charlietap.chasm.compiler.backend.dex.abi.ART_BYTE_ARRAY_BASE_OFFSET
import io.github.charlietap.chasm.compiler.driver.ClassNames
import io.github.charlietap.chasm.compiler.driver.ModuleSources
import io.github.charlietap.chasm.compiler.driver.TrapSiteSource
import io.github.charlietap.chasm.compiler.frontend.UncompilableFunctions
import io.github.charlietap.chasm.compiler.frontend.diagnostic.TrapSiteTables
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.decoder.WasmFunctionBodyOffsets
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.dex.CompileError.Stage
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.module.ModuleDeclarationsFactory
import io.github.charlietap.chasm.validator.WasmModuleValidator
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest
import java.util.TimeZone
import java.util.jar.JarFile
import java.util.zip.ZipEntry
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CompileTest {

    private val directory: Path = Files.createTempDirectory("dex-compile")

    @AfterTest
    fun cleanUp() {
        directory.toFile().deleteRecursively()
    }

    @Test
    fun `a module compiles to DEX files and the resources it needs`() = runTest {

        val bytes = module("declarations")
        val output = DexOutput.memory()

        val info = compile(bytes, output, CompileConfig("test.compiled.declarations.Module")).expect("compiles")

        assertEquals(List(output.dexFiles.size) { index -> if (index == 0) "classes.dex" else "classes${index + 1}.dex" }, info.dexFiles)
        assertEquals(listOf("test/compiled/declarations/Module.chasmdata"), info.resources)
        assertTrue(output.resources.getValue("test/compiled/declarations/Module.chasmdata").isNotEmpty())
        assertTrue("test.compiled.declarations.Module" in info.classes)
        assertTrue(info.classes.all { name -> name.startsWith("test.compiled.declarations.Module") })
        assertTrue("Ltest/compiled/declarations/Module;" in classTypes(output.dexFiles))
        assertEquals(COMPILED_RUNTIME_ABI, info.runtimeAbi)
        assertEquals(sha256(bytes), info.sourceHash)
        assertNull(info.diagnostics)
    }

    @Test
    fun `a module without data segments writes no data resource`() = runTest {

        val output = DexOutput.memory()

        val info = compile(EMPTY_MODULE, output, CompileConfig("test.empty.Module")).expect("compiles")

        assertTrue(info.resources.isEmpty())
        assertTrue(output.resources.isEmpty())
        assertEquals(listOf("classes.dex"), info.dexFiles)
    }

    @Test
    fun `stack traces add the trap sites resource`() = runTest {

        val traced = DexOutput.memory()
        val plain = DexOutput.memory()

        val tracedConfig = CompileConfig("test.sites.Module", RuntimeConfig(stacktraces = true))
        val tracedInfo = compile(module("trap_diagnostics"), traced, tracedConfig).expect("compiles")
        val plainInfo = compile(module("trap_diagnostics"), plain, CompileConfig("test.sites.Module")).expect("compiles")

        assertEquals(Diagnostics("test/sites/Module.sites"), tracedInfo.diagnostics)
        assertTrue("test/sites/Module.sites" in tracedInfo.resources)
        assertTrue(traced.resources.getValue("test/sites/Module.sites").isNotEmpty())
        assertNull(plainInfo.diagnostics)
        assertTrue(plain.resources.keys.none { path -> path.endsWith(".sites") })
    }

    @Test
    fun `an invalid class name is a configuration error`() = runTest {

        val output = DexOutput.memory()

        val result = compile(EMPTY_MODULE, output, CompileConfig("test.1invalid.Module"))

        assertEquals(Stage.CONFIGURATION, assertIs<ChasmResult.Error<CompileError>>(result).error.stage)
        assertTrue(output.dexFiles.isEmpty())
    }

    @Test
    fun `each unreadable, malformed or invalid input reports its stage`() = runTest {

        val missing = compile(directory.resolve("missing.wasm"), DexOutput.memory(), CompileConfig("test.missing.Module"))
        val malformed = compile(byteArrayOf(1, 2, 3), DexOutput.memory(), CompileConfig("test.malformed.Module"))
        val invalid = compile(module("invalid"), DexOutput.memory(), CompileConfig("test.invalid.Module"))

        assertEquals(Stage.INPUT, assertIs<ChasmResult.Error<CompileError>>(missing).error.stage)
        assertEquals(Stage.DECODE, assertIs<ChasmResult.Error<CompileError>>(malformed).error.stage)
        assertEquals(Stage.VALIDATION, assertIs<ChasmResult.Error<CompileError>>(invalid).error.stage)
    }

    @Test
    fun `an unsupported function is a generation error naming it`() = runTest {

        val result = compile(module("simd"), DexOutput.memory(), CompileConfig("test.simd.Module"))

        val error = assertIs<ChasmResult.Error<CompileError>>(result).error
        assertEquals(Stage.GENERATION, error.stage)
        assertEquals(listOf(2), error.functions)
        assertTrue("2 (" in error.message, error.message)
    }

    @Test
    fun `a module with a 64-bit memory is a generation error`() = runTest {

        val output = DexOutput.memory()

        val result = compile(module("memory64"), output, CompileConfig("test.memory64.Module"))

        val expected = CompileError("memory64 is unsupported", Stage.GENERATION)
        assertEquals(expected, assertIs<ChasmResult.Error<CompileError>>(result).error)
        assertTrue(output.dexFiles.isEmpty())
    }

    @Test
    fun `a generation error lists ten functions and counts the rest`() = runTest {

        val result = compile(module("many_simd"), DexOutput.memory(), CompileConfig("test.many.Module"))

        val error = assertIs<ChasmResult.Error<CompileError>>(result).error
        assertEquals((0 until 12).toList(), error.functions)
        assertTrue(error.message.endsWith(" and 2 more"), error.message)
        assertFalse("10 (" in error.message, error.message)
    }

    @Test
    fun `a failed compile writes nothing`() = runTest {

        val classes = directory.resolve("classes")
        val jar = directory.resolve("module.jar")
        val memory = DexOutput.memory()

        val config = CompileConfig("test.failed.Module")

        assertIs<ChasmResult.Error<CompileError>>(compile(module("simd"), DexOutput.directory(classes), config))
        assertIs<ChasmResult.Error<CompileError>>(compile(module("simd"), DexOutput.jar(jar), config))
        assertIs<ChasmResult.Error<CompileError>>(compile(module("simd"), memory, config))

        assertFalse(Files.exists(classes))
        assertFalse(Files.exists(jar))
        assertTrue(memory.dexFiles.isEmpty() && memory.resources.isEmpty())
        Files.list(directory).use { files -> assertEquals(0L, files.count()) }
    }

    @Test
    fun `compiling a module twice gives identical bytes`() = runTest {

        val first = DexOutput.memory()
        val second = DexOutput.memory()

        compile(module("managed_objects"), first, CompileConfig("test.twice.Module")).expect("compiles first")
        compile(module("managed_objects"), second, CompileConfig("test.twice.Module")).expect("compiles second")

        assertEquals(first.dexFiles.size, second.dexFiles.size)
        first.dexFiles.zip(second.dexFiles).forEach { (expected, actual) -> assertContentEquals(expected, actual) }
        assertEquals(first.resources.keys, second.resources.keys)
        first.resources.forEach { (path, bytes) -> assertContentEquals(bytes, second.resources.getValue(path), path) }
    }

    @Test
    fun `a module compiles to the same output from a file as from its bytes`() = runTest {

        val bytes = module("declarations")
        val file = Files.write(directory.resolve("declarations.wasm"), bytes)
        val fromFile = DexOutput.memory()
        val fromBytes = DexOutput.memory()

        compile(file, fromFile, CompileConfig("test.same.Module")).expect("compiles from the file")
        compile(bytes, fromBytes, CompileConfig("test.same.Module")).expect("compiles from the bytes")

        fromBytes.dexFiles.zip(fromFile.dexFiles).forEach { (expected, actual) -> assertContentEquals(expected, actual) }
        assertEquals(fromBytes.resources.keys, fromFile.resources.keys)
    }

    @Test
    fun `a jar holds the DEX files and the resources`() = runTest {

        val jar = directory.resolve("module.jar")

        val info = compile(module("declarations"), DexOutput.jar(jar), CompileConfig("test.jar.Module")).expect("compiles")

        JarFile(jar.toFile()).use { file ->
            val names = file.entries().toList().map { entry -> entry.name }
            assertEquals(info.dexFiles + info.resources, names)
            assertEquals(ZipEntry.STORED, file.getJarEntry("test/jar/Module.chasmdata").method)
        }
    }

    @Test
    fun `a directory holds the DEX files and the resources`() = runTest {

        val classes = directory.resolve("classes")

        val info = compile(module("declarations"), DexOutput.directory(classes), CompileConfig("test.directory.Module")).expect("compiles")

        (info.dexFiles + info.resources).forEach { path -> assertTrue(Files.isRegularFile(classes.resolve(path)), path) }
    }

    @Test
    fun `an empty directory takes a compile`() = runTest {

        val classes = Files.createDirectory(directory.resolve("classes"))

        compile(EMPTY_MODULE, DexOutput.directory(classes), CompileConfig("test.empty.Module")).expect("compiles")

        assertTrue(Files.isRegularFile(classes.resolve("classes.dex")))
    }

    @Test
    fun `a non-empty directory is an output error`() = runTest {

        val classes = Files.createDirectory(directory.resolve("classes"))
        val stale = Files.write(classes.resolve("classes3.dex"), byteArrayOf(1))

        val result = compile(EMPTY_MODULE, DexOutput.directory(classes), CompileConfig("test.stale.Module"))

        val error = assertIs<ChasmResult.Error<CompileError>>(result).error
        assertEquals(Stage.OUTPUT, error.stage)
        assertTrue("is not empty" in error.message && "DexOutput.directory" in error.message, error.message)
        assertFalse(Files.exists(classes.resolve("classes.dex")))
        assertContentEquals(byteArrayOf(1), Files.readAllBytes(stale))
    }

    @Test
    fun `a directory write that fails part way removes what it wrote`() = runTest {

        val classes = directory.resolve("classes")
        val staged = StagedDexOutput(DexOutput.directory(classes))
        staged.dexFile("classes.dex", byteArrayOf(1))
        staged.resource("classes.dex/Module.chasmdata") { stream -> stream.write(2) }

        val failure = staged.commit().getError()

        assertIs<IOException>(failure)
        assertFalse(Files.exists(classes))
        compile(EMPTY_MODULE, DexOutput.directory(classes), CompileConfig("test.retried.Module")).expect("compiles again")
    }

    @Test
    fun `written DEX files, resources and jars are read-only`() = runTest {

        val classes = directory.resolve("classes")
        val jar = directory.resolve("module.jar")

        val info = compile(module("declarations"), DexOutput.directory(classes), CompileConfig("test.readonly.Module")).expect("compiles")
        compile(module("declarations"), DexOutput.jar(jar), CompileConfig("test.readonly.Module")).expect("compiles")

        (info.dexFiles + info.resources).forEach { path -> assertFalse(classes.resolve(path).toFile().canWrite(), path) }
        assertFalse(jar.toFile().canWrite())
    }

    @Test
    fun `a jar is the same whatever the default time zone`() = runTest {

        val first = directory.resolve("first.jar")
        val second = directory.resolve("second.jar")
        val zone = TimeZone.getDefault()

        try {
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
            compile(module("declarations"), DexOutput.jar(first), CompileConfig("test.zone.Module")).expect("compiles in UTC")
            TimeZone.setDefault(TimeZone.getTimeZone("Pacific/Kiritimati"))
            compile(module("declarations"), DexOutput.jar(second), CompileConfig("test.zone.Module")).expect("compiles in UTC+14")
        } finally {
            TimeZone.setDefault(zone)
        }

        assertContentEquals(Files.readAllBytes(first), Files.readAllBytes(second))
    }

    @Test
    fun `a jar is replaced by the next compile`() = runTest {

        val jar = directory.resolve("module.jar")

        compile(module("declarations"), DexOutput.jar(jar), CompileConfig("test.first.Module")).expect("compiles first")
        compile(EMPTY_MODULE, DexOutput.jar(jar), CompileConfig("test.second.Module")).expect("compiles second")

        JarFile(jar.toFile()).use { file ->
            assertEquals(listOf("classes.dex"), file.entries().toList().map { entry -> entry.name })
        }
        Files.list(directory).use { files -> assertEquals(listOf(jar), files.toList()) }
    }

    @Test
    fun `a module that declares a memory and never reads it is unpinned`() = runTest {

        val unread = DexOutput.memory()
        val read = DexOutput.memory()

        compile(UNREAD_MEMORY, unread, CompileConfig("test.unread.Module")).expect("compiles the unread memory")
        compile(module("declarations"), read, CompileConfig("test.read.Module")).expect("compiles the read memory")

        assertEquals(UNPINNED_OFFSET, byteArrayBaseOffset(unread.dexFiles, "Ltest/unread/Module;"))
        assertEquals(ART_BYTE_ARRAY_BASE_OFFSET, byteArrayBaseOffset(read.dexFiles, "Ltest/read/Module;"))
    }

    @Test
    fun `cancelling a compile stops it and writes nothing`() = runTest {

        val output = DexOutput.memory()
        val lowering = CompletableDeferred<Unit>()
        val lowerer: ParallelModuleLowerer = { _, _, _, _, _, resources, _ ->
            resources.resource("test/cancelled/Module.chasmdata") { stream -> stream.write(1) }
            lowering.complete(Unit)
            awaitCancellation()
        }
        val compilation = DexCompilation(
            sources = ModuleSources(::WasmModuleDecoder, ::WasmModuleDecoder, ::WasmModuleValidator),
            classNames = ClassNames(),
            frontend = ::DexFrontend,
            uncompilableFunctions = ::UncompilableFunctions,
            declarationsFactory = ::ModuleDeclarationsFactory,
            trapSites = TrapSiteSource(::WasmFunctionBodyOffsets, ::TrapSiteTables),
            lowerer = lowerer,
            executor = compileExecutor,
        )

        val job = launch(Dispatchers.Default) {
            compilation.compile(Input.Bytes(module("declarations")), output, CompileConfig("test.cancelled.Module"))
        }
        lowering.await()
        job.cancelAndJoin()

        assertTrue(job.isCancelled)
        assertTrue(output.dexFiles.isEmpty() && output.resources.isEmpty())
    }

    private fun module(name: String): ByteArray {

        val stream = checkNotNull(CompileTest::class.java.getResourceAsStream("/modules/$name.wasm")) { "missing $name" }

        return stream.use { input -> input.readBytes() }
    }

    private fun classDefs(dexFiles: List<ByteArray>): List<DexBackedClassDef> {
        return dexFiles.flatMap { bytes -> DexBackedDexFile(Opcodes.forApi(MIN_API), bytes).classes }
    }

    private fun classTypes(dexFiles: List<ByteArray>): Set<String> {
        return classDefs(dexFiles).map { classDef -> classDef.type }.toSet()
    }

    private fun byteArrayBaseOffset(
        dexFiles: List<ByteArray>,
        entry: String,
    ): Long {

        val entryClass = classDefs(dexFiles).single { classDef -> classDef.type == entry }
        val method = entryClass.methods.single { candidate -> candidate.name == "byteArrayBaseOffset" }

        return checkNotNull(method.implementation).instructions.filterIsInstance<WideLiteralInstruction>().single().wideLiteral
    }

    private fun sha256(bytes: ByteArray): String {
        return MessageDigest.getInstance("SHA-256").digest(bytes).toHexString()
    }

    private companion object {
        const val MIN_API = 27
        val EMPTY_MODULE = byteArrayOf(0x00, 0x61, 0x73, 0x6D, 0x01, 0x00, 0x00, 0x00)
        val UNREAD_MEMORY = byteArrayOf(0x00, 0x61, 0x73, 0x6D, 0x01, 0x00, 0x00, 0x00, 0x05, 0x03, 0x01, 0x00, 0x01)
    }
}
