package io.github.charlietap.chasm.aot.script

import com.github.michaelbull.result.fold
import io.github.charlietap.chasm.aot.CompiledModule
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.compiler.backend.jvm.JvmModule
import io.github.charlietap.chasm.compiler.backend.jvm.JvmOptions
import io.github.charlietap.chasm.compiler.backend.jvm.JvmOutput
import io.github.charlietap.chasm.compiler.backend.jvm.MemoryLowering
import io.github.charlietap.chasm.compiler.backend.jvm.ModuleLowerer
import io.github.charlietap.chasm.compiler.backend.jvm.UnsupportedFunctions
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.module.ModuleDeclarationsFactory
import io.github.charlietap.chasm.runtime.execution.CompiledStack
import io.github.charlietap.chasm.validator.WasmModuleValidator
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.URL
import java.nio.ByteBuffer
import java.nio.file.Files
import java.util.concurrent.ConcurrentHashMap

internal object ScriptModules {

    private const val TEST_STACK_BYTES = 32L shl 20

    private val SPLIT_INSTRUCTIONS: Int? = System.getProperty("chasm.test.aot.split")?.toInt()

    private val MEMORY: MemoryLowering = System.getProperty("chasm.test.aot.memory")?.let(MemoryLowering::valueOf) ?: MemoryLowering.HELPERS

    private val compiled = ConcurrentHashMap<CompileKey, ChasmResult<CompiledModule, ChasmError>>()

    fun useTestStack() {
        CompiledStack.budgetSource = { CompiledStack.budgetFor(TEST_STACK_BYTES) }
    }

    fun decode(
        bytes: ByteArray,
        config: ModuleConfig,
    ): ChasmResult<Module, ChasmError> = WasmModuleDecoder(config, bytes).fold(
        { module -> ChasmResult.Success(module) },
        { error -> ChasmResult.Error(ChasmError.DecodeError(error.toString())) },
    )

    fun validate(
        bytes: ByteArray,
        config: ModuleConfig,
    ): ChasmResult<Module, ChasmError> = when (val decoded = decode(bytes, config)) {
        is ChasmResult.Error -> decoded
        is ChasmResult.Success -> WasmModuleValidator(config, decoded.result).fold(
            { module -> ChasmResult.Success(module) },
            { error -> ChasmResult.Error(ChasmError.ValidationError(error.toString())) },
        )
    }

    fun compile(
        bytes: ByteArray,
        config: ModuleConfig,
        storeConfig: StoreConfig,
    ): ChasmResult<CompiledModule, ChasmError> {
        val options = JvmOptions(
            meterFuel = storeConfig.meterFuel,
            interruptible = storeConfig.interruptible,
            splitInstructions = SPLIT_INSTRUCTIONS,
            memory = MEMORY,
        )
        return compiled.getOrPut(CompileKey(ByteBuffer.wrap(bytes.copyOf()), config, options)) {
            when (val validated = validate(bytes, config)) {
                is ChasmResult.Error -> validated
                is ChasmResult.Success -> try {
                    ChasmResult.Success(lower(validated.result, options))
                } catch (failure: IllegalStateException) {
                    ChasmResult.Error(ChasmError.ExecutionError(failure.message ?: failure.toString()))
                }
            }
        }
    }

    private fun lower(
        module: Module,
        options: JvmOptions,
    ): CompiledModule {
        val cir = ModuleCompiler(module).fold({ it }) { error -> throw IllegalStateException("cannot compile: $error") }
        val generated = GeneratedCode()
        val result = try {
            ModuleLowerer(module, cir, options, generated, ModuleDeclarationsFactory(module))
        } catch (unsupported: UnsupportedFunctions) {
            throw IllegalStateException("AOT: ${unsupported.message}")
        }
        return generated.compiledModule(result)
    }
}

private data class CompileKey(
    val bytes: ByteBuffer,
    val config: ModuleConfig,
    val options: JvmOptions,
)

private class GeneratedCode : ClassLoader(GeneratedCode::class.java.classLoader), JvmOutput {

    private val classes = ConcurrentHashMap<String, ByteArray>()
    private val resources = ConcurrentHashMap<String, ByteArray>()
    private val files = ConcurrentHashMap<String, URL>()
    private val directory by lazy {
        Files.createTempDirectory("chasm-aot-resources").also { directory -> directory.toFile().deleteOnExit() }
    }

    override fun classFile(
        internalName: String,
        bytes: ByteArray,
    ) {
        check(classes.put(internalName, bytes) == null) { "class $internalName generated twice" }
    }

    override fun <T> resource(
        path: String,
        write: (OutputStream) -> T,
    ): T {
        val stream = ByteArrayOutputStream()
        val result = write(stream)
        check(resources.put(path, stream.toByteArray()) == null) { "resource $path generated twice" }
        return result
    }

    fun compiledModule(
        module: JvmModule,
    ): CompiledModule =
        loadClass(module.moduleClass).getField("INSTANCE").get(null) as CompiledModule

    override fun findClass(
        name: String,
    ): Class<*> {
        val bytes = classes[name.replace('.', '/')] ?: throw ClassNotFoundException(name)
        return defineClass(name, bytes, 0, bytes.size)
    }

    override fun getResourceAsStream(
        name: String,
    ): InputStream? =
        resources[name.removePrefix("/")]?.inputStream() ?: super.getResourceAsStream(name)

    override fun findResource(
        name: String,
    ): URL? = resources[name]?.let { bytes ->
        files.computeIfAbsent(name) {
            directory.resolve(name.replace('/', '_')).also { file ->
                file.toFile().deleteOnExit()
                Files.write(file, bytes)
            }.toUri().toURL()
        }
    }
}
