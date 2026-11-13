package io.github.charlietap.chasm.aot.script

import com.github.michaelbull.result.fold
import io.github.charlietap.chasm.aot.CompiledModule
import io.github.charlietap.chasm.aot.instance
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.typeResolver
import io.github.charlietap.chasm.compiled.Instantiable
import io.github.charlietap.chasm.compiled.script.ScriptModules
import io.github.charlietap.chasm.compiler.backend.jvm.JvmModule
import io.github.charlietap.chasm.compiler.backend.jvm.JvmOptions
import io.github.charlietap.chasm.compiler.backend.jvm.JvmOutput
import io.github.charlietap.chasm.compiler.backend.jvm.MemoryLowering
import io.github.charlietap.chasm.compiler.backend.jvm.ModuleLowerer
import io.github.charlietap.chasm.compiler.backend.jvm.UnsupportedFunctions
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.chasm.compiler.frontend.ModuleRewriter
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.map
import io.github.charlietap.chasm.module.ModuleDeclarationsFactory
import io.github.charlietap.chasm.runtime.execution.CompiledStack
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.URL
import java.nio.ByteBuffer
import java.nio.file.Files
import java.util.concurrent.ConcurrentHashMap

internal object AotCompiler {

    private const val TEST_STACK_BYTES = 32L shl 20

    private val SPLIT_INSTRUCTIONS: Int? = System.getProperty("chasm.test.aot.split")?.toInt()

    private val MEMORY: MemoryLowering = System.getProperty("chasm.test.aot.memory")?.let(MemoryLowering::valueOf) ?: JvmOptions().memory

    private val compiled = ConcurrentHashMap<CompileKey, ChasmResult<CompiledModule, ChasmError>>()

    fun useTestStack() {
        CompiledStack.budgetSource = { CompiledStack.budgetFor(TEST_STACK_BYTES) }
    }

    fun compile(
        bytes: ByteArray,
        config: ModuleConfig,
        storeConfig: StoreConfig,
    ): ChasmResult<Instantiable, ChasmError> {
        val options = JvmOptions(
            storeConfig = storeConfig,
            splitInstructions = SPLIT_INSTRUCTIONS,
            memory = MEMORY,
        )
        return compiled.getOrPut(CompileKey(ByteBuffer.wrap(bytes.copyOf()), config, options)) {
            when (val validated = ScriptModules.validate(bytes, config)) {
                is ChasmResult.Error -> validated
                is ChasmResult.Success -> try {
                    ChasmResult.Success(lower(validated.result, options))
                } catch (failure: IllegalStateException) {
                    ChasmResult.Error(ChasmError.ExecutionError(failure.message ?: failure.toString()))
                }
            }
        }.map { module -> { store, imports -> instance(store, module, imports) } }
    }

    private fun lower(
        module: Module,
        options: JvmOptions,
    ): CompiledModule {
        val types = module.typeResolver()
        val source = ModuleRewriter(
            preserveSource = options.stacktraces || options.meterFuel || options.interruptible,
            inlineLeaves = false,
        ).rewrite(module, types)
        val cir = ModuleCompiler(source, types).fold({ it }) { error -> throw IllegalStateException("cannot compile: $error") }
        val generated = GeneratedCode()
        val result = try {
            ModuleLowerer(source, cir, options, generated, ModuleDeclarationsFactory(source))
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
        Files.createTempDirectory("runtime-aot-resources").also { directory -> directory.toFile().deleteOnExit() }
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
