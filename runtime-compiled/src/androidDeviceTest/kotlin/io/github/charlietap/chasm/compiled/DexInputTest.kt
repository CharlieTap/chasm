package io.github.charlietap.chasm.compiled

import android.os.Build
import com.github.michaelbull.result.getOrElse
import dalvik.system.InMemoryDexClassLoader
import io.github.charlietap.chasm.ast.module.typeResolver
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.compiler.backend.dex.DexOptions
import io.github.charlietap.chasm.compiler.backend.dex.DexResources
import io.github.charlietap.chasm.compiler.backend.dex.ParallelModuleLowerer
import io.github.charlietap.chasm.compiler.backend.dex.PoolLimits
import io.github.charlietap.chasm.compiler.backend.dex.abi.ART_BYTE_ARRAY_BASE_OFFSET
import io.github.charlietap.chasm.compiler.backend.dex.lowerWithSeparateMetadata
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.dex.CompileConfig
import io.github.charlietap.chasm.dex.DexOutput
import io.github.charlietap.chasm.dex.compile
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.value.NumberValue.I32
import io.github.charlietap.chasm.embedding.value.NumberValue.I64
import io.github.charlietap.chasm.module.ModuleDeclarationsFactory
import io.github.charlietap.chasm.parallel.coroutines.CallerParticipatingParallelTaskExecutor
import io.github.charlietap.chasm.validator.WasmModuleValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import org.junit.Assume.assumeTrue
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import io.github.charlietap.chasm.ast.module.Module as WasmModule

class DexInputTest {

    private val directory: Path = Files.createTempDirectory("dex-input")

    @AfterTest
    fun cleanUp() {
        directory.toFile().deleteRecursively()
    }

    @Test
    fun aDexFileOutsideTheClassesRunIsNotLoaded() {

        val compiled = directory.resolve("module")
        val className = dexClassName()
        val outsider = dexClassName()
        compileInto(DexOutput.directory(compiled), className)
        Files.write(compiled.resolve("classes3.dex"), dexOutput(resourceBytes(ADD), outsider).dexFiles.single())

        assertEquals(LOADED, load(module(DexInput.directory(compiled), className).expect("loads the run")))
        assertMalformed("class $outsider was not found", module(DexInput.directory(compiled), outsider))
    }

    @Test
    fun aHelperClassTheParentDefinesFailsTheLoad() {

        val className = "io.github.charlietap.chasm.compiled.ShadowedCode"
        val output = dexOutput(resourceBytes(PATCH), className, TRACED)

        val result = module(DexInput.memory(output.dexFiles, output.resources), className)

        assertMalformed("class $className\$Code0 of the module", result)
    }

    @Test
    fun aSignatureInterfaceTheParentDefinesFailsTheLoad() {

        val className = "io.github.charlietap.chasm.compiled.ShadowedSignature"
        val output = dexOutput(resourceBytes(PATCH), className, TRACED)

        val result = module(DexInput.memory(output.dexFiles, output.resources), className)

        assertMalformed("class $className\$Sig0 of the module", result)
    }

    @Test
    fun mutatingMemoryInputArraysBeforeModuleChangesNothing() {

        val className = dexClassName()
        val output = dexOutput(resourceBytes(DATA), className)
        val input = DexInput.memory(output.dexFiles, output.resources)

        output.dexFiles.forEach { bytes -> bytes.fill(0) }
        output.resources.values.forEach { bytes -> bytes.fill(0) }

        assertEquals(LOADED, load(module(input, className).expect("loads")))
    }

    @Test
    fun dataSegmentsWithoutADataResourceAreMalformed() {

        val className = dexClassName()
        val output = dexOutput(resourceBytes(DATA), className)

        val result = module(DexInput.memory(output.dexFiles), className)

        assertMalformed("resource ${dataPath(className)} is missing", result)
    }

    @Test
    fun memoryDexLoads() {

        val className = dexClassName()
        val output = dexOutput(resourceBytes(DATA), className)

        assertEquals(LOADED, load(module(DexInput.memory(output.dexFiles, output.resources), className).expect("loads")))
    }

    @Test
    fun aDirectoryLoadsWithItsData() {

        val compiled = directory.resolve("module")
        val className = dexClassName()
        compileInto(DexOutput.directory(compiled), className)

        assertTrue(Files.isRegularFile(compiled.resolve(dataPath(className))))
        assertEquals(LOADED, load(module(DexInput.directory(compiled), className).expect("loads")))
    }

    @Test
    fun aJarLoads() {

        val jar = directory.resolve("module.jar")
        val className = dexClassName()
        compileInto(DexOutput.jar(jar), className)

        assertEquals(LOADED, load(module(DexInput.jar(jar), className).expect("loads")))
    }

    @Test
    fun aWritableDexFileIsMalformedOnApi34() {

        assumeTrue(Build.VERSION.SDK_INT >= READ_ONLY_DEX_SDK)
        val compiled = directory.resolve("module")
        val className = dexClassName()
        compileInto(DexOutput.directory(compiled), className)
        val dexFile = compiled.resolve("classes.dex").toFile()
        assertTrue(dexFile.setWritable(true))

        assertMalformed("$dexFile is writable", module(DexInput.directory(compiled), className))
    }

    @Test
    fun anEmptyDirectoryIsMalformed() {

        val empty = Files.createDirectory(directory.resolve("empty"))

        assertMalformed("$empty holds no classes.dex", module(DexInput.directory(empty), dexClassName()))
    }

    @Test
    fun anotherByteArrayOffsetIsMalformed() {

        val className = dexClassName()
        val dexFiles = lowerWithOffset(resourceBytes(DATA), className, OTHER_BYTE_ARRAY_BASE)

        val result = module(DexInput.memory(dexFiles.dex, dexFiles.resources), className)

        assertMalformed("the module reads byte arrays at offset $OTHER_BYTE_ARRAY_BASE but this VM keeps them at", result)
    }

    @Test
    fun aModuleWhoseMetadataSpansSeveralDexFilesLoads() {

        val className = dexClassName()
        val bytes = resourceBytes(SPLIT_METADATA)
        val wasm = decode(bytes)
        val resources = CollectedResources()
        val options = DexOptions(entryClass = className, runtime = RuntimeConfig(), byteArrayBaseOffset = ART_BYTE_ARRAY_BASE_OFFSET)
        val dex = lowerWithSeparateMetadata(wasm, frontend(wasm), options, ModuleDeclarationsFactory(wasm), resources, TINY_POOLS)

        val loaded = module(DexInput.memory(dex.dex, resources.resources), className).expect("loads")
        val instance = runBlocking { instance(store(loaded), loaded) }.expect("instantiates")

        assertTrue(dex.dex.size > MIN_SPLIT_FILES, "${dex.dex.size} DEX files")
        assertEquals(listOf(I64(SPLIT_POINT)), invoke(instance, "point", listOf(I32(2), I64(10))).expect("point"))
        assertEquals(listOf(I32(SPLIT_FIXED)), invoke(instance, "fixed", listOf(I32(5))).expect("fixed"))
    }

    @Test
    fun aModuleLoadsIntoTwoStores() {

        val module = dexModule(DATA_MODULE)

        assertEquals(LOADED, load(module, store(module)))
        assertEquals(LOADED, load(module, store(module)))
    }

    @Test
    fun twoModulesWithTheSameClassNameLoadSideBySide() {

        val className = dexClassName()
        val first = dexOutput(resourceBytes(DATA), className)
        val second = dexOutput(resourceBytes(DATA), className)
        val firstModule = module(DexInput.memory(first.dexFiles, first.resources), className).expect("loads the first")
        val secondModule = module(DexInput.memory(second.dexFiles, second.resources), className).expect("loads the second")

        assertEquals(LOADED, load(firstModule))
        assertEquals(LOADED, load(secondModule))

        val shared = store(firstModule)
        runBlocking { instance(shared, firstModule) }.expect("instantiates the first")
        val result = runBlocking { instance(shared, secondModule) }
        assertIs<ExecutionError.Unlinkable>(assertIs<ChasmResult.Error<*>>(result).error)
    }

    @Test
    fun aCallersInMemoryLoaderHasNoData() {

        val className = dexClassName()
        val output = dexOutput(resourceBytes(DATA), className)
        val buffers = Array(output.dexFiles.size) { index -> ByteBuffer.wrap(output.dexFiles[index]) }
        val loader = InMemoryDexClassLoader(buffers, CompiledModule::class.java.classLoader)

        assertMalformed("resource ${dataPath(className)} is missing", module(loader, className))
    }

    private fun compileInto(
        output: DexOutput,
        className: String,
    ) {
        runBlocking { compile(resourceBytes(DATA), output, CompileConfig(className)) }.expect("compiles")
    }

    private fun load(
        module: Module,
        store: Store = store(module),
    ): Int {

        val instance = runBlocking { instance(store, module) }.expect("instantiates")

        return (invoke(instance, "load").expect("load").single() as I32).value
    }

    private fun assertMalformed(
        reason: String,
        result: ChasmResult<Module, ModuleError>,
    ) {

        val error = assertIs<ModuleError.Malformed>(assertIs<ChasmResult.Error<ModuleError>>(result).error)

        assertTrue(error.toString().contains(reason), error.toString())
    }

    private fun dataPath(className: String): String {
        return className.replace('.', '/') + ".chasmdata"
    }

    private fun lowerWithOffset(
        bytes: ByteArray,
        className: String,
        offset: Long,
    ): LoweredDex {

        val module = decode(bytes)
        val code = frontend(module)
        val resources = CollectedResources()
        val executor = CallerParticipatingParallelTaskExecutor(Dispatchers.Default, Semaphore(LOWERING_WORKERS))
        val options = DexOptions(entryClass = className, runtime = RuntimeConfig(), byteArrayBaseOffset = offset)

        val dex = runBlocking {
            ParallelModuleLowerer(module, code, options, ModuleDeclarationsFactory(module), emptyMap(), resources, executor)
        }

        return LoweredDex(dex.dex, resources.resources)
    }

    private fun decode(bytes: ByteArray): WasmModule {

        val config = ModuleConfig()
        val decoded = WasmModuleDecoder(config, bytes).getOrElse { failure -> error("$failure") }

        return WasmModuleValidator(config, decoded).getOrElse { failure -> error("$failure") }
    }

    private fun frontend(module: WasmModule): CirModule {
        return ModuleCompiler(module, module.typeResolver(), recordProvenance = false, devirtualiseIndirectCalls = false)
            .getOrElse { failure -> error("$failure") }
    }

    private data class LoweredDex(
        val dex: List<ByteArray>,
        val resources: Map<String, ByteArray>,
    )

    private class CollectedResources : DexResources {

        val resources = HashMap<String, ByteArray>()

        override fun <T> resource(
            path: String,
            write: (OutputStream) -> T,
        ): T {

            val stream = ByteArrayOutputStream()
            val result = write(stream)
            resources[path] = stream.toByteArray()

            return result
        }
    }

    private companion object {
        const val ADD = "modules/add.wasm"
        const val DATA = "modules/data_segment.wasm"
        const val DATA_MODULE = "data_segment"
        const val PATCH = "modules/patch.wasm"
        const val LOADED = 42
        const val READ_ONLY_DEX_SDK = 34
        const val OTHER_BYTE_ARRAY_BASE = 16L
        const val LOWERING_WORKERS = 2
        const val SPLIT_METADATA = "modules/split_metadata.wasm"
        const val SPLIT_POINT = 13L
        const val SPLIT_FIXED = 8
        const val MIN_SPLIT_FILES = 3
        val TRACED = RuntimeConfig(stacktraces = true)
        val TINY_POOLS = PoolLimits(strings = 72, types = 32, fields = 32, methods = 32)
    }
}
