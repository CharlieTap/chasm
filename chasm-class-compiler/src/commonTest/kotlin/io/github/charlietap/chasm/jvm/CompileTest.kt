package io.github.charlietap.chasm.jvm

import io.github.charlietap.chasm.compiled.CompiledModule
import io.github.charlietap.chasm.compiled.format.COMPILED_RUNTIME_ABI
import io.github.charlietap.chasm.compiled.format.TARGET_ART
import io.github.charlietap.chasm.compiled.format.TARGET_HOTSPOT
import io.github.charlietap.chasm.compiled.format.TRAP_SITES_EXTENSION
import io.github.charlietap.chasm.compiled.format.UNPINNED_OFFSET
import io.github.charlietap.chasm.compiled.format.UNSET_LONG
import io.github.charlietap.chasm.compiled.format.readTrapSites
import io.github.charlietap.chasm.compiled.format.trapSitesResource
import io.github.charlietap.chasm.compiler.backend.jvm.JvmModule
import io.github.charlietap.chasm.compiler.backend.jvm.JvmOutput
import io.github.charlietap.chasm.compiler.backend.jvm.ModuleLowerer
import io.github.charlietap.chasm.compiler.backend.jvm.size.DexUnitEstimate
import io.github.charlietap.chasm.compiler.driver.ClassNames
import io.github.charlietap.chasm.compiler.driver.ModuleSources
import io.github.charlietap.chasm.compiler.driver.TrapSiteSource
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.chasm.compiler.frontend.UncompilableFunctions
import io.github.charlietap.chasm.compiler.frontend.diagnostic.TrapSiteTables
import io.github.charlietap.chasm.config.CpuLimits
import io.github.charlietap.chasm.config.GrowthFailureStrategy
import io.github.charlietap.chasm.config.KB
import io.github.charlietap.chasm.config.Limits
import io.github.charlietap.chasm.config.MemoryLimits
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.config.bytes
import io.github.charlietap.chasm.config.fuel
import io.github.charlietap.chasm.config.pages
import io.github.charlietap.chasm.decoder.WasmFunctionBodyOffsets
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.jvm.CompileError.Stage
import io.github.charlietap.chasm.module.ModuleDeclarationsFactory
import io.github.charlietap.chasm.validator.WasmModuleValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.test.runTest
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.LabelNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest
import java.util.concurrent.CountDownLatch
import java.util.jar.JarFile
import java.util.zip.ZipEntry
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue
import io.github.charlietap.chasm.embedding.module as embeddingModule

private val EMPTY_MODULE = byteArrayOf(0x00, 0x61, 0x73, 0x6d, 0x01, 0x00, 0x00, 0x00)

class CompileTest {

    @Test
    fun `a compiled module lists its class and the data resource it wrote`() = runTest {

        val output = ClassOutput.memory()

        val info = compile(module("declarations"), output, CompileConfig("test.compiled.declarations.Module")).expect("compiles")

        assertTrue("test.compiled.declarations.Module" in info.classes)
        assertTrue(info.classes.all { name -> name == "test.compiled.declarations.Module" || name.startsWith("test.compiled.declarations.Module\$") })
        assertTrue("test/compiled/declarations/Module.chasmdata" in info.resources)
        assertTrue(output.resources.getValue("test/compiled/declarations/Module.chasmdata").isNotEmpty())
    }

    @Test
    fun `a module without data segments writes no data resource`() = runTest {

        val output = ClassOutput.memory()

        val info = compile(EMPTY_MODULE, output, CompileConfig("test.empty.Module")).expect("compiles")

        assertTrue(info.resources.none { path -> path.endsWith(".chasmdata") })
        assertTrue(output.resources.keys.none { path -> path.endsWith(".chasmdata") })
    }

    @Test
    fun `a module compiles to the same output from a file as from its bytes`() = runTest {
        val bytes = module("declarations")
        val file = Files.createTempFile("module", ".wasm").also { Files.write(it, bytes) }
        val fromFile = ClassOutput.memory()
        val fromBytes = ClassOutput.memory()

        compile(file, fromFile, CompileConfig("test.same.Module")).expect("compiles from the file")
        compile(bytes, fromBytes, CompileConfig("test.same.Module")).expect("compiles from the bytes")
        Files.delete(file)

        assertEquals(fromBytes.entries().keys, fromFile.entries().keys)
        fromBytes.entries().forEach { (name, contents) -> assertContentEquals(contents, fromFile.entries().getValue(name), name) }
    }

    @Test
    fun `an Android module reaches memory as Android's memories allow`() = runTest {
        val output = ClassOutput.memory()
        val config = CompileConfig("test.android.Module", CompileTarget.ART)

        compile(module("declarations"), output, config).expect("compiles")

        assertTrue(output.classFiles.values.any { bytes -> "sun/misc/Unsafe" in String(bytes, Charsets.ISO_8859_1) })
    }

    @Test
    fun `each target sizes its methods for the machine that runs them`() = runTest {

        val bytes = longFunctionModule(LARGE_STEPS)
        val jvm = ClassOutput.memory()
        val android = ClassOutput.memory()

        compile(bytes, jvm, CompileConfig("test.sized.jvm.Module")).expect("compiles for the JVM")
        compile(bytes, android, CompileConfig("test.sized.android.Module", CompileTarget.ART)).expect("compiles for Android")

        val jvmMethods = functionMethods(jvm)
        val androidMethods = functionMethods(android)
        assertTrue(jvmMethods.size > 1 && androidMethods.size > 1, "the function was not split")
        assertTrue(jvmMethods.maxOf { (_, length) -> length } <= 8_000)
        assertTrue(androidMethods.maxOf { (method, length) -> DexUnitEstimate().of(method, length) } <= 5_150)
    }

    @Test
    fun `a function needing more chunks than one dispatcher holds fails generation`() {

        val limits = listOf(CompileTarget.ART to "5150 estimated DEX code units", CompileTarget.HOTSPOT to "32767 bytes")

        limits.forEach { (target, limit) ->

            val result = refusedDispatcher(ClassOutput.memory(), CompileConfig("test.dispatcher.Module", target))

            assertIs<ChasmResult.Error<CompileError>>(result)
            assertEquals(Stage.GENERATION, result.error.stage, "$target")
            assertEquals(listOf(0), result.error.functions, "$target")
            assertTrue(Regex("cannot generate functions: 0 \\(function needs \\d+ chunks, more than one dispatcher of $limit holds\\)").matches(result.error.message), result.error.message)
        }
    }

    @Test
    fun `failures say where compilation failed`() = runTest {
        val missing = Files.createTempDirectory("compile").resolve("missing.wasm")
        val blocked = Files.createTempFile("compile", ".blocked")
        val cases = listOf(
            compile(module("declarations"), ClassOutput.memory(), CompileConfig("test.class.Module")) to Stage.CONFIGURATION,
            compile(module("declarations"), ClassOutput.memory(), CompileConfig("1test.Module")) to Stage.CONFIGURATION,
            compile(missing, ClassOutput.memory(), CompileConfig("test.missing.Module")) to Stage.INPUT,
            compile(byteArrayOf(1, 2, 3), ClassOutput.memory(), CompileConfig("test.garbage.Module")) to Stage.DECODE,
            compile(module("invalid"), ClassOutput.memory(), CompileConfig("test.invalid.Module")) to Stage.VALIDATION,
            compile(module("simd"), ClassOutput.memory(), CompileConfig("test.simd.Module")) to Stage.GENERATION,
            compile(module("declarations"), ClassOutput.directory(blocked.resolve("classes")), CompileConfig("test.failing.Module")) to Stage.OUTPUT,
        )
        Files.delete(blocked)

        cases.forEach { (result, stage) ->
            assertIs<ChasmResult.Error<CompileError>>(result)
            assertEquals(stage, result.error.stage, result.error.toString())
        }
        val simd = assertIs<ChasmResult.Error<CompileError>>(cases.single { (_, stage) -> stage == Stage.GENERATION }.first)
        assertEquals(listOf(2), simd.error.functions, simd.error.toString())
        assertTrue("Simd" in simd.error.message, simd.error.toString())
    }

    @Test
    fun `a name that is not a Java class name is a configuration error`() = runTest {
        listOf("a..B", "a.class.B", "a.B\$C", "").forEach { name ->

            val result = compile(module("declarations"), ClassOutput.memory(), CompileConfig(name))

            assertIs<ChasmResult.Error<CompileError>>(result)
            assertEquals(CompileError("\"$name\" is not a Java class name", Stage.CONFIGURATION), result.error)
        }
    }

    @Test
    fun `two modules compiled into one package do not share a class or a resource`() = runTest {

        val shared = ClassOutput.memory()
        val first = ClassOutput.memory()
        val second = ClassOutput.memory()
        val firstConfig = CompileConfig("test.shared.First", runtime = TRACED)
        val secondConfig = CompileConfig("test.shared.Second", runtime = TRACED)

        listOf(first to firstConfig, second to secondConfig, shared to firstConfig, shared to secondConfig).forEach { (output, config) ->
            compile(module("declarations"), output, config).expect("compiles")
        }

        assertEquals(emptySet(), first.entries().keys intersect second.entries().keys)
        assertEquals(first.entries().keys + second.entries().keys, shared.entries().keys)
        assertTrue(first.classFiles.keys.all { name -> name == "test.shared.First" || name.startsWith("test.shared.First\$") })
        assertTrue(second.classFiles.keys.all { name -> name == "test.shared.Second" || name.startsWith("test.shared.Second\$") })
        assertEquals(
            setOf("test/shared/First.chasmdata", trapSitesResource("test/shared/First"), "META-INF/proguard/test.shared.First.pro"),
            first.resources.keys,
        )
        assertEquals(
            setOf("test/shared/Second.chasmdata", trapSitesResource("test/shared/Second"), "META-INF/proguard/test.shared.Second.pro"),
            second.resources.keys,
        )
    }

    @Test
    fun `the generated class reports the settings it was compiled with`() = runTest {

        val runtime = RuntimeConfig(
            interrupts = true,
            stacktraces = true,
            limits = Limits(
                cpu = CpuLimits.Metered(1_000.fuel),
                memory = MemoryLimits(
                    linearMemory = 16.pages,
                    gcHeap = 1.KB,
                    tables = 256.bytes,
                    stack = 64.KB,
                    onGrowthFailure = GrowthFailureStrategy.Trap,
                ),
            ),
        )
        val configured = CompileConfig("test.configured.Module", runtime = runtime)
        val metered = CompileConfig("test.metered.Module", runtime = RuntimeConfig(limits = Limits(cpu = CpuLimits.Metered(0.fuel))))
        val defaults = CompileConfig("test.defaults.Module")
        val directory = Files.createTempDirectory("compile")
        val outputs = listOf<suspend (CompileConfig) -> CompiledModule>(
            { config ->
                val memory = ClassOutput.memory()
                val info = compile(module("declarations"), memory, config).expect("compiles into memory")
                MemoryLoader(memory).loadClass(info.config.className).getField("COMPILED").get(null) as CompiledModule
            },
            { config ->
                val classes = directory.resolve(config.className)
                loaded(classes, compile(module("declarations"), ClassOutput.directory(classes), config).expect("compiles into a directory"))
            },
            { config ->
                val jar = directory.resolve("${config.className}.jar")
                loaded(jar, compile(module("declarations"), ClassOutput.jar(jar), config).expect("compiles into a jar"))
            },
        )

        outputs.forEach { load ->

            val module = load(configured)
            assertEquals(COMPILED_RUNTIME_ABI, module.runtimeAbi())
            assertTrue(module.interrupts())
            assertTrue(module.stacktraces())
            assertEquals(1_000L, module.fuel())
            assertEquals(16L * 65_536L, module.linearMemoryBytes())
            assertEquals(1_024L, module.gcHeapBytes())
            assertEquals(32L * 8L, module.tableBytes())
            assertEquals(65_536L, module.stackBytes())
            assertTrue(module.trapOnGrowthFailure())
            assertEquals("test/configured/Module", module.resourceRoot())

            assertEquals(0L, load(metered).fuel())

            val unset = load(defaults)
            assertFalse(unset.interrupts())
            assertFalse(unset.stacktraces())
            assertEquals(UNSET_LONG, unset.fuel())
            assertEquals(UNSET_LONG, unset.linearMemoryBytes())
            assertEquals(UNSET_LONG, unset.gcHeapBytes())
            assertEquals(UNSET_LONG, unset.tableBytes())
            assertEquals(UNSET_LONG, unset.stackBytes())
            assertFalse(unset.trapOnGrowthFailure())
            assertEquals("test/defaults/Module", unset.resourceRoot())
        }
    }

    @Test
    fun `the entry class exposes itself as COMPILED and implements CompiledModule`() = runTest {

        val output = ClassOutput.memory()
        compile(module("declarations"), output, CompileConfig("test.entry.Module")).expect("compiles")
        val entry = ClassNode()
        ClassReader(output.classFiles.getValue("test.entry.Module")).accept(entry, 0)

        val field = entry.fields.single { field -> field.name == "COMPILED" }
        val initializer = entry.methods.single { method -> method.name == "<clinit>" }
        val calls = initializer.instructions.filterIsInstance<MethodInsnNode>().map { call -> "${call.owner}.${call.name}" }

        assertEquals(Opcodes.ACC_PUBLIC or Opcodes.ACC_STATIC or Opcodes.ACC_FINAL, field.access)
        assertEquals("Ltest/entry/Module;", field.desc)
        assertEquals(listOf(Type.getInternalName(CompiledModule::class.java)), entry.interfaces)
        assertEquals(listOf("test/entry/Module.<init>"), calls)
    }

    @Test
    fun `a module that starts above the limits baked into it fails compilation`() = runTest {

        val memory = CompileConfig("test.memory.limit.Module", runtime = memoryLimits(MemoryLimits(linearMemory = 65_535.bytes)))
        val table = CompileConfig("test.table.limit.Module", runtime = memoryLimits(MemoryLimits(tables = 15.bytes)))
        val fits = CompileConfig("test.fits.Module", runtime = memoryLimits(MemoryLimits(linearMemory = 1.pages, tables = 16.bytes)))

        val memoryResult = compile(module("declarations"), ClassOutput.memory(), memory)
        val tableResult = compile(module("declarations"), ClassOutput.memory(), table)

        assertIs<ChasmResult.Error<CompileError>>(memoryResult)
        assertEquals(CompileError("memory 0 starts at 1 pages, above the limit of 65535 bytes", Stage.CONFIGURATION), memoryResult.error)
        assertIs<ChasmResult.Error<CompileError>>(tableResult)
        assertEquals(CompileError("table 0 starts at 2 elements, above the limit of 15 bytes", Stage.CONFIGURATION), tableResult.error)
        assertIs<ChasmResult.Success<CompiledModuleInfo>>(compile(module("declarations"), ClassOutput.memory(), fits))
    }

    @Test
    fun `a module with a 64-bit memory is a generation error`() = runTest {

        val output = ClassOutput.memory()

        val result = compile(module("memory64"), output, CompileConfig("test.memory64.Module"))

        val expected = CompileError("memory64 is unsupported", Stage.GENERATION)
        assertEquals(expected, assertIs<ChasmResult.Error<CompileError>>(result).error)
        assertTrue(output.classFiles.isEmpty())
    }

    @Test
    fun `metered fuel and limits are baked into the generated class`() = runTest {

        val runtime = RuntimeConfig(
            limits = Limits(
                cpu = CpuLimits.Metered(Long.MAX_VALUE.fuel),
                memory = MemoryLimits(linearMemory = 100_000.bytes, tables = 20.bytes, stack = 1_000.bytes),
            ),
        )
        val output = ClassOutput.memory()

        val info = compile(module("declarations"), output, CompileConfig("test.baked.Module", runtime = runtime)).expect("compiles")
        val module = MemoryLoader(output).loadClass(info.config.className).getField("COMPILED").get(null) as CompiledModule

        assertEquals(Long.MAX_VALUE, module.fuel())
        assertEquals(100_000L, module.linearMemoryBytes())
        assertEquals(20L, module.tableBytes())
        assertEquals(1_000L, module.stackBytes())
        assertEquals(UNSET_LONG, module.gcHeapBytes())
        assertFalse(module.trapOnGrowthFailure())
    }

    @Test
    fun `a config is plain data`() {

        val config = CompileConfig("test.data.Module")
        val android = config.copy(target = CompileTarget.ART, runtime = TRACED)

        assertEquals(CompileTarget.HOTSPOT, config.target)
        assertEquals(RuntimeConfig(), config.runtime)
        assertEquals(CompileConfig("test.data.Module"), config)
        assertEquals(CompileConfig("test.data.Module", CompileTarget.ART, TRACED), android)
        assertEquals("test.data.Module", android.className)
    }

    @Test
    fun `a compiled module lists every class and resource it wrote`() = runTest {

        val memory = ClassOutput.memory()
        val directory = Files.createTempDirectory("compile").resolve("classes")
        val config = CompileConfig("test.listed.Module", runtime = TRACED)

        val fromMemory = compile(module("declarations"), memory, config).expect("compiles into memory")
        val fromDirectory = compile(module("declarations"), ClassOutput.directory(directory), config).expect("compiles into a directory")

        val written = Files.walk(directory).use { files ->
            files.filter(Files::isRegularFile).map { file -> directory.relativize(file).toString() }.toList().toSet()
        }
        assertEquals(memory.classFiles.keys.toList(), fromMemory.classes)
        assertEquals(memory.resources.keys.toList(), fromMemory.resources)
        assertEquals(fromMemory.classes, fromDirectory.classes)
        assertEquals(fromMemory.resources, fromDirectory.resources)
        assertEquals(fromDirectory.classes.map { name -> "${name.replace('.', '/')}.class" }.toSet() + fromDirectory.resources, written)
    }

    @Test
    fun `a caller's own output receives every class and resource`() = runTest {

        val output = RecordingClassOutput()

        val info = compile(module("trap_diagnostics"), output, CompileConfig("test.own.Module", runtime = TRACED)).expect("compiles")

        assertEquals(info.classes, output.classFiles)
        assertEquals(info.resources, output.resources)
    }

    @Test
    fun `a caller's own output is told nothing more after a failure`() {

        val output = RecordingClassOutput()

        val result = refusedDispatcher(output, CompileConfig("test.own.failed.Module", runtime = TRACED))

        assertIs<ChasmResult.Error<CompileError>>(result)
        assertEquals(Stage.GENERATION, result.error.stage)
        assertTrue(output.classFiles.isNotEmpty())
        assertTrue(output.resources.none { path -> path.startsWith("META-INF/") })
    }

    @Test
    fun `class names are binary names`() = runTest {

        val directory = Files.createTempDirectory("compile").resolve("classes")

        val info = compile(module("trap_diagnostics"), ClassOutput.directory(directory), CompileConfig("test.binary.Module")).expect("compiles")

        val loader = URLClassLoader(arrayOf(directory.toUri().toURL()), CompileTest::class.java.classLoader)
        assertTrue(info.classes.any { name -> '$' in name })
        info.classes.forEach { name ->
            assertEquals(name, Class.forName(name, false, loader).name)
        }
    }

    @Test
    fun `a module too large for one jar fails and points at a directory`() {

        val directory = Files.createTempDirectory("compile")
        val full = directory.resolve("full.jar")
        val fullOutput = JarClassOutput(full)
        val overOutput = JarClassOutput(directory.resolve("over.jar"))

        repeat(65_534) { index ->
            fullOutput.classFile("test.full.C$index", byteArrayOf(0))
        }
        fullOutput.commit()
        repeat(65_534) { index ->
            overOutput.classFile("test.over.C$index", byteArrayOf(0))
        }
        assertFailsWith<JarEntryLimitExceeded> { overOutput.classFile("test.over.C65534", byteArrayOf(0)) }
        overOutput.discard()

        val bytes = Files.readAllBytes(full)
        val zip64Locator = byteArrayOf(0x50, 0x4b, 0x06, 0x07)
        val endRecords = bytes.copyOfRange(maxOf(0, bytes.size - 256), bytes.size)
        val hasZip64Locator = (0..endRecords.size - zip64Locator.size).any { index ->
            endRecords.copyOfRange(index, index + zip64Locator.size).contentEquals(zip64Locator)
        }
        JarFile(full.toFile()).use { file -> assertEquals(65_534, file.size()) }
        assertFalse(hasZip64Locator)
        assertEquals(listOf(full), Files.list(directory).use { files -> files.toList() })
        Files.delete(full)

        val result = compileWith(ClassOutput.jar(directory.resolve("module.jar")), CompileConfig("test.large.Module")) { jvmOutput ->
            repeat(65_535) { index ->
                jvmOutput.classFile("test/large/Module\$C$index", byteArrayOf(0))
            }
        }

        assertIs<ChasmResult.Error<CompileError>>(result)
        assertEquals(
            CompileError(
                "the module needs more than 65,534 jar entries, which one jar cannot hold: write it with ClassOutput.directory(...)",
                Stage.OUTPUT,
            ),
            result.error,
        )
        assertEquals(emptyList(), Files.list(directory).use { files -> files.toList() })
    }

    @Test
    fun `shrinker rules travel in the artifact only when there are any`() = runTest {

        val traced = ClassOutput.memory()
        val plain = ClassOutput.memory()
        val path = "META-INF/proguard/test.rules.travel.Module.pro"

        val tracedInfo = compile(module("trap_diagnostics"), traced, CompileConfig("test.rules.travel.Module", runtime = TRACED)).expect("compiles")
        val plainInfo = compile(module("trap_diagnostics"), plain, CompileConfig("test.rules.travel.Module")).expect("compiles")

        assertTrue(tracedInfo.shrinkerRules.isNotEmpty())
        assertEquals(tracedInfo.shrinkerRules, traced.resources.getValue(path).decodeToString())
        assertEquals(path, tracedInfo.resources.last())
        assertEquals("", plainInfo.shrinkerRules)
        assertTrue(plain.resources.keys.none { name -> name.startsWith("META-INF/") })
        assertTrue(plainInfo.resources.none { name -> name.startsWith("META-INF/") })
    }

    @Test
    fun `the info describes the module`() = runTest {

        val bytes = module("trap_diagnostics")
        val file = Files.createTempFile("module", ".wasm").also { file -> Files.write(file, bytes) }
        val config = CompileConfig("test.info.Module")
        val tracedConfig = CompileConfig("test.info.traced.Module", runtime = TRACED)

        val fromBytes = compile(bytes, ClassOutput.memory(), config).expect("compiles from the bytes")
        val fromFile = compile(file, ClassOutput.memory(), config).expect("compiles from the file")
        val traced = compile(bytes, ClassOutput.memory(), tracedConfig).expect("compiles with stack traces")
        Files.delete(file)

        val decoded = embeddingModule(bytes, ModuleConfig(decodeNameSection = true)).expect("decodes")
        val sha256 = MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { byte -> "%02x".format(byte) }
        assertEquals(config, fromBytes.config)
        assertTrue(decoded.imports.isNotEmpty())
        assertEquals(decoded.imports, fromBytes.imports)
        assertEquals(decoded.exports, fromBytes.exports)
        assertEquals(sha256, fromBytes.sourceHash)
        assertEquals(sha256, fromFile.sourceHash)
        assertEquals(5, fromBytes.runtimeAbi)
        assertTrue(fromBytes.compilerVersion.isNotBlank())
        assertEquals(null, fromBytes.diagnostics)
        assertEquals(Diagnostics("test/info/traced/Module.sites", "test/info/traced/Module.lines"), traced.diagnostics)
    }

    @Test
    fun `the memory output holds what was written and cannot load it`() = runTest {

        val output = ClassOutput.memory()

        val info = compile(module("declarations"), output, CompileConfig("test.memory.Module")).expect("compiles")

        assertEquals(info.classes.toSet(), output.classFiles.keys)
        assertEquals(info.resources.toSet(), output.resources.keys)
        output.classFiles.forEach { (name, bytes) ->
            assertEquals(name.replace('.', '/'), ClassReader(bytes).className)
        }
    }

    @Test
    fun `a jar output replaces the jar an earlier compilation left`() = runTest {

        val directory = Files.createTempDirectory("compile")
        val jar = directory.resolve("module.jar")
        Files.write(jar, byteArrayOf(1))

        compile(module("declarations"), ClassOutput.jar(jar), CompileConfig("test.jar.Module")).expect("compiles")

        JarFile(jar.toFile()).use { file -> assertTrue(file.getEntry("test/jar/Module.class") != null) }
        assertEquals(setOf("module.jar"), Files.list(directory).use { files -> files.map { path -> path.fileName.toString() }.toList().toSet() })
    }

    @Test
    fun `a jar stores the data resource uncompressed and compresses everything else`() = runTest {

        val jar = Files.createTempDirectory("compile").resolve("module.jar")

        compile(module("declarations"), ClassOutput.jar(jar), CompileConfig("test.stored.Module", runtime = RuntimeConfig(stacktraces = true)))
            .expect("compiles")

        JarFile(jar.toFile()).use { file ->

            val data = file.getJarEntry("test/stored/Module.chasmdata")
            val others = file.entries().toList().filter { entry -> entry.name != data.name }

            assertEquals(ZipEntry.STORED, data.method)
            assertEquals(data.size, data.compressedSize)
            assertTrue(others.isNotEmpty())
            assertTrue(others.all { entry -> entry.method == ZipEntry.DEFLATED }, others.map { entry -> entry.name to entry.method }.toString())
        }
    }

    @Test
    fun `a jar output fails and then compiles into a valid jar`() = runTest {

        val directory = Files.createTempDirectory("compile")
        val jar = directory.resolve("module.jar")
        val output = ClassOutput.jar(jar)

        assertIs<ChasmResult.Error<CompileError>>(refusedDispatcher(output, CompileConfig("test.reused.jar.Failed")))
        compile(module("declarations"), output, CompileConfig("test.reused.jar.Module")).expect("compiles")

        JarFile(jar.toFile()).use { file -> assertTrue(file.getEntry("test/reused/jar/Module.class") != null) }
        assertEquals(listOf(jar), Files.list(directory).use { files -> files.toList() })
    }

    @Test
    fun `a directory output fails and then compiles every class`() = runTest {

        val directory = Files.createTempDirectory("compile").resolve("classes")
        val output = ClassOutput.directory(directory)

        assertIs<ChasmResult.Error<CompileError>>(refusedDispatcher(output, CompileConfig("test.reused.directory.Failed")))
        val info = compile(module("declarations"), output, CompileConfig("test.reused.directory.Module")).expect("compiles")

        val written = Files.walk(directory).use { files ->
            files.filter(Files::isRegularFile).map { file -> directory.relativize(file).toString() }.toList().toSet()
        }
        assertEquals(info.classes.map { name -> "${name.replace('.', '/')}.class" }.toSet() + info.resources, written)
    }

    @Test
    fun `a lowerer that throws leaves no temporary file behind`() {

        val directory = Files.createTempDirectory("compile")
        val outputs = listOf(ClassOutput.jar(directory.resolve("module.jar")), ClassOutput.directory(directory.resolve("classes")))

        outputs.forEach { output ->
            assertFailsWith<IllegalStateException> {
                compileWith(output, CompileConfig("test.thrown.Module")) { jvmOutput ->
                    jvmOutput.classFile("test/thrown/Module", byteArrayOf(0))
                    throw IllegalStateException("lowering failed")
                }
            }
        }

        assertEquals(emptyList(), Files.walk(directory).use { files -> files.filter(Files::isRegularFile).toList() })
    }

    @Test
    fun `a jar compiles to the same bytes every time`() = runTest {
        val directory = Files.createTempDirectory("compile")

        compile(module("declarations"), ClassOutput.jar(directory.resolve("a.jar")), CompileConfig("test.same.jar.Module")).expect("compiles")
        compile(module("declarations"), ClassOutput.jar(directory.resolve("b.jar")), CompileConfig("test.same.jar.Module")).expect("compiles")

        assertContentEquals(Files.readAllBytes(directory.resolve("a.jar")), Files.readAllBytes(directory.resolve("b.jar")))
    }

    @Test
    fun `a failed compilation leaves earlier output alone`() = runTest {
        val directory = Files.createTempDirectory("compile")
        val jar = directory.resolve("module.jar")
        Files.write(jar, byteArrayOf(1, 2, 3))
        val memory = ClassOutput.memory()

        assertIs<ChasmResult.Error<CompileError>>(compile(module("simd"), ClassOutput.jar(jar), CompileConfig("test.failed.Module")))
        assertIs<ChasmResult.Error<CompileError>>(compile(module("simd"), memory, CompileConfig("test.failed.Module")))
        assertIs<ChasmResult.Error<CompileError>>(compile(module("simd"), ClassOutput.directory(directory.resolve("classes")), CompileConfig("test.failed.Module")))

        assertContentEquals(byteArrayOf(1, 2, 3), Files.readAllBytes(jar))
        assertEquals(setOf("module.jar"), Files.list(directory).use { files -> files.map { it.fileName.toString() }.toList().toSet() })
        assertTrue(memory.classFiles.isEmpty())
    }

    @Test
    fun `a stock output drops what a compilation wrote before it failed`() {

        val directory = Files.createTempDirectory("compile")
        val jar = directory.resolve("module.jar")
        Files.write(jar, byteArrayOf(1, 2, 3))
        val memory = ClassOutput.memory()
        val config = CompileConfig("test.dropped.Module", runtime = TRACED)

        listOf(ClassOutput.jar(jar), memory, ClassOutput.directory(directory.resolve("classes"))).forEach { output ->
            assertIs<ChasmResult.Error<CompileError>>(refusedDispatcher(output, config))
        }

        assertContentEquals(byteArrayOf(1, 2, 3), Files.readAllBytes(jar))
        assertEquals(listOf(jar), Files.walk(directory).use { files -> files.filter(Files::isRegularFile).toList() })
        assertTrue(memory.classFiles.isEmpty())
        assertTrue(memory.resources.isEmpty())
    }

    @Test
    fun `stacktraces record the offset of every trap and call site`() = runTest {
        val output = ClassOutput.memory()

        compile(module("trap_diagnostics"), output, CompileConfig("test.sites.Module", runtime = TRACED)).expect("compiles")

        val sites = checkNotNull(readTrapSites(output.resources.getValue(trapSitesResource("test/sites/Module")).inputStream()))
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
    fun `an Android module records which inline access failed when stacktraces are on`() = runTest {
        val plain = ClassOutput.memory()
        val diagnosed = ClassOutput.memory()

        compile(module("trap_diagnostics"), plain, CompileConfig("test.android.plain.Module", CompileTarget.ART)).expect("compiles")
        val config = CompileConfig("test.android.sites.Module", CompileTarget.ART, TRACED)
        compile(module("trap_diagnostics"), diagnosed, config).expect("compiles with stacktraces")

        fun MemoryClassOutput.records(): Boolean {
            return classFiles.values.any { bytes -> "memoryAccessOutOfBounds" in String(bytes, Charsets.ISO_8859_1) }
        }

        assertTrue(diagnosed.records())
        assertTrue(!plain.records())
    }

    @Test
    fun `a module compiled without stacktraces writes no trap sites and no shrinker rules`() = runTest {
        val output = ClassOutput.memory()

        val info = compile(module("trap_diagnostics"), output, CompileConfig("test.plain.Module")).expect("compiles")

        assertTrue(output.resources.keys.none { name -> name.endsWith(".$TRAP_SITES_EXTENSION") })
        assertEquals("", info.shrinkerRules)
    }

    @Test
    fun `stacktraces keep the compiled functions' names and line numbers through a shrinker`() = runTest {
        val config = CompileConfig("test.rules.Module", runtime = TRACED)

        val info = compile(module("trap_diagnostics"), ClassOutput.memory(), config).expect("compiles")

        assertEquals(
            "-keepattributes LineNumberTable\n" +
                "-keep class test.rules.Module\$Code*, test.rules.Module\$Chunks* { static *** f*(...); }\n" +
                "-keep,allowobfuscation,allowshrinking class io.github.charlietap.chasm.runtime.compiled.gc.ManagedEntry " +
                "{ *** enter(...); }\n",
            info.shrinkerRules,
        )
    }

    @Test
    fun `the artifact records its target`() = runTest {

        val hotspot = ClassOutput.memory()
        val art = ClassOutput.memory()

        val hotspotInfo = compile(module("declarations"), hotspot, CompileConfig("test.target.hotspot.Module"))
            .expect("compiles for HotSpot")
        compile(module("declarations"), art, CompileConfig("test.target.art.Module", CompileTarget.ART)).expect("compiles for ART")

        val hotspotModule = MemoryLoader(hotspot).loadClass("test.target.hotspot.Module").getField("COMPILED").get(null) as CompiledModule
        val artModule = MemoryLoader(art).loadClass("test.target.art.Module").getField("COMPILED").get(null) as CompiledModule
        assertEquals(TARGET_HOTSPOT, hotspotModule.target())
        assertEquals(TARGET_ART, artModule.target())
        assertEquals(UNPINNED_OFFSET, hotspotModule.byteArrayBaseOffset())
        assertEquals(UNPINNED_OFFSET, artModule.byteArrayBaseOffset())
        assertFalse(hotspotModule.initializesEagerly())
        assertFalse(artModule.initializesEagerly())
        assertEquals(hotspotInfo.classes.toSet() - "test.target.hotspot.Module", hotspotModule.classes().toSet())
        assertEquals(hotspotInfo.classes.size - 1, hotspotModule.classes().size)
        assertEquals(emptyList(), artModule.classes().toList())
    }

    @Test
    fun `cancelling a compile discards the staged output`() = runTest {

        val directory = Files.createTempDirectory("compile")
        val memory = ClassOutput.memory()
        val outputs = listOf(ClassOutput.jar(directory.resolve("module.jar")), memory, ClassOutput.directory(directory.resolve("classes")))

        outputs.forEach { output ->
            val started = CountDownLatch(1)
            val compilation = launch(Dispatchers.Default) {
                runInterruptible {
                    compileWith(output, CompileConfig("test.cancelled.Module")) { jvmOutput ->
                        jvmOutput.classFile("test/cancelled/Module", byteArrayOf(0))
                        started.countDown()
                        Thread.sleep(Long.MAX_VALUE)
                    }
                }
            }
            runInterruptible(Dispatchers.IO) { started.await() }
            compilation.cancelAndJoin()
        }

        assertEquals(emptyList(), Files.walk(directory).use { files -> files.filter(Files::isRegularFile).toList() })
        assertTrue(memory.classFiles.isEmpty())
    }

    @Test
    fun `a compilation whose thread was interrupted commits nothing`() {

        val memory = ClassOutput.memory()

        Thread.currentThread().interrupt()
        try {
            assertFailsWith<InterruptedException> {
                compileWith(memory, CompileConfig("test.interrupted.Module")) { jvmOutput ->
                    jvmOutput.classFile("test/interrupted/Module", byteArrayOf(0))
                }
            }
        } finally {
            Thread.interrupted()
        }

        assertTrue(memory.classFiles.isEmpty())
    }

    private fun module(
        name: String,
    ): ByteArray =
        checkNotNull(CompileTest::class.java.getResourceAsStream("/modules/$name.wasm")) { "missing module $name" }.use { it.readBytes() }

    private fun memoryLimits(memory: MemoryLimits): RuntimeConfig {
        return RuntimeConfig(limits = Limits(memory = memory))
    }

    private fun refusedDispatcher(
        output: ClassOutput,
        config: CompileConfig,
    ): ChasmResult<CompiledModuleInfo, CompileError> {

        val bytes = longFunctionModule(REFUSED_STEPS)

        return compile(
            output = output,
            config = config,
            classNames = ClassNames(),
            sources = ModuleSources(::WasmModuleDecoder, ::WasmModuleDecoder, ::WasmModuleValidator),
            read = { sources -> sources.read(bytes, MemoryLimits()) },
            moduleCompiler = { module, types -> ModuleCompiler(module, types, recordProvenance = false, devirtualiseIndirectCalls = true) },
            uncompilableFunctions = ::UncompilableFunctions,
            declarationsFactory = ::ModuleDeclarationsFactory,
            trapSiteSource = TrapSiteSource(::WasmFunctionBodyOffsets, ::TrapSiteTables),
            moduleLowerer = { module, code, options, jvmOutput, declarations, sites ->
                ModuleLowerer(module, code, options.copy(splitInstructions = 1), jvmOutput, declarations, sites)
            },
        )
    }

    private fun compileWith(
        output: ClassOutput,
        config: CompileConfig,
        lower: (JvmOutput) -> Unit,
    ): ChasmResult<CompiledModuleInfo, CompileError> {
        return compile(
            output = output,
            config = config,
            classNames = ClassNames(),
            sources = ModuleSources(::WasmModuleDecoder, ::WasmModuleDecoder, ::WasmModuleValidator),
            read = { sources -> sources.read(EMPTY_MODULE, MemoryLimits()) },
            moduleCompiler = { module, types -> ModuleCompiler(module, types, recordProvenance = false, devirtualiseIndirectCalls = true) },
            uncompilableFunctions = ::UncompilableFunctions,
            declarationsFactory = ::ModuleDeclarationsFactory,
            trapSiteSource = TrapSiteSource(::WasmFunctionBodyOffsets, ::TrapSiteTables),
            moduleLowerer = { _, _, _, jvmOutput, _, _ ->
                lower(jvmOutput)
                JvmModule(config.className)
            },
        )
    }

    private fun functionMethods(output: MemoryClassOutput): List<Pair<MethodNode, Int>> {
        return output.classFiles.values.flatMap { bytes ->

            val reader = ClassReader(bytes)
            val owner = ClassNode()
            reader.accept(owner, 0)

            owner.methods.filter { method -> FUNCTION_METHOD.matches(method.name) }.map { method ->
                val end = LabelNode()
                method.instructions.add(end)
                val writer = ClassWriter(reader, 0)
                writer.visit(owner.version, owner.access, owner.name, owner.signature, owner.superName, owner.interfaces.toTypedArray())
                method.accept(writer)
                method to end.label.offset
            }
        }
    }

    private fun longFunctionModule(steps: Int): ByteArray {

        val body = ArrayList<Byte>()
        body.bytes(0x00, 0x20, 0x00, 0x41, 0x01, 0x20, 0x01, 0x6e, 0x6a, 0x21, 0x00)
        repeat(steps) { step ->
            if (step % 8 == 7) {
                body.bytes(0x20, 0x00, 0x41, 0x00, 0x48, 0x04, 0x40, 0x20, 0x00, 0x41, 0x03, 0x6a, 0x21, 0x00, 0x0b)
            } else {
                body.bytes(0x20, 0x00, 0x41, step % 5 + 1, 0x6a, 0x21, 0x00)
            }
        }
        body.bytes(0x20, 0x00, 0x0b)

        val module = ArrayList<Byte>()
        module.bytes(0x00, 0x61, 0x73, 0x6d, 0x01, 0x00, 0x00, 0x00)
        module.section(1, listOf(0x01, 0x60, 0x02, 0x7f, 0x7f, 0x01, 0x7f).map(Int::toByte))
        module.section(3, listOf<Byte>(0x01, 0x00))
        module.section(7, listOf(0x01, 0x03, 'r'.code, 'u'.code, 'n'.code, 0x00, 0x00).map(Int::toByte))
        module.section(
            10,
            ArrayList<Byte>().apply {
                bytes(0x01)
                leb(body.size)
                addAll(body)
            },
        )

        return module.toByteArray()
    }

    private fun ArrayList<Byte>.bytes(vararg values: Int) {
        values.forEach { value -> add(value.toByte()) }
    }

    private fun ArrayList<Byte>.leb(value: Int) {

        var rest = value
        do {
            val low = rest and 0x7f
            rest = rest ushr 7
            add((if (rest != 0) low or 0x80 else low).toByte())
        } while (rest != 0)
    }

    private fun ArrayList<Byte>.section(
        id: Int,
        content: List<Byte>,
    ) {
        bytes(id)
        leb(content.size)
        addAll(content)
    }

    private fun MemoryClassOutput.entries(): Map<String, ByteArray> {
        return classFiles.mapKeys { (name, _) -> "$name.class" } + resources
    }

    private fun loaded(
        path: Path,
        info: CompiledModuleInfo,
    ): CompiledModule {
        val loader = URLClassLoader(arrayOf(path.toUri().toURL()), CompileTest::class.java.classLoader)
        return loader.loadClass(info.config.className).getField("COMPILED").get(null) as CompiledModule
    }

    private class MemoryLoader(
        private val output: MemoryClassOutput,
    ) : ClassLoader(CompileTest::class.java.classLoader) {

        override fun findClass(name: String): Class<*> {
            val bytes = output.classFiles[name] ?: throw ClassNotFoundException(name)
            return defineClass(name, bytes, 0, bytes.size)
        }
    }

    private class RecordingClassOutput : ClassOutput {

        val classFiles = ArrayList<String>()
        val resources = ArrayList<String>()

        override fun classFile(
            className: String,
            bytes: ByteArray,
        ) {
            classFiles += className
        }

        override fun resource(
            path: String,
            bytes: ByteArray,
        ) {
            resources += path
        }
    }

    private companion object {
        const val LARGE_STEPS = 20_000
        const val REFUSED_STEPS = 4_000
        val FUNCTION_METHOD = Regex("f\\d+(\\\$c\\d+)?")
        val TRACED = RuntimeConfig(stacktraces = true)
    }
}
