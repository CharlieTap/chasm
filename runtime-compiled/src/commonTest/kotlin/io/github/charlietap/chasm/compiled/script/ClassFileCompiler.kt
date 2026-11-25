package io.github.charlietap.chasm.compiled.script

import com.github.michaelbull.result.fold
import io.github.charlietap.chasm.ast.module.typeResolver
import io.github.charlietap.chasm.compiled.ScriptFailure
import io.github.charlietap.chasm.compiled.internal.platformTarget
import io.github.charlietap.chasm.compiled.loadClassFiles
import io.github.charlietap.chasm.compiler.backend.analysis.UnsupportedFunctions
import io.github.charlietap.chasm.compiler.backend.jvm.JvmOptions
import io.github.charlietap.chasm.compiler.backend.jvm.JvmOutput
import io.github.charlietap.chasm.compiler.backend.jvm.MemoryLowering
import io.github.charlietap.chasm.compiler.backend.jvm.ModuleLowerer
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.module.ModuleDeclarationsFactory
import io.github.charlietap.chasm.runtime.execution.CompiledStack
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import io.github.charlietap.chasm.ast.module.Module as WasmModule

internal object ClassFileCompiler {

    private const val TEST_STACK_BYTES = 32L shl 20

    private const val ENTRY_PACKAGE = "chasm.compiled.script"

    private val SPLIT_INSTRUCTIONS: Int? = System.getProperty("chasm.test.aot.split")?.toInt()

    private val MEMORY: MemoryLowering = System.getProperty("chasm.test.aot.memory")?.let(MemoryLowering::valueOf) ?: JvmOptions().memory

    private val compiled = ConcurrentHashMap<CompileKey, ChasmResult<Module, ScriptFailure>>()

    private val modules = AtomicInteger()

    fun useTestStack() {
        CompiledStack.budgetSource = { CompiledStack.budgetFor(TEST_STACK_BYTES) }
    }

    fun compile(
        bytes: ByteArray,
        config: ModuleConfig,
        storeConfig: RuntimeConfig,
    ): ChasmResult<Module, ScriptFailure> {

        val options = JvmOptions(
            runtime = storeConfig,
            splitInstructions = SPLIT_INSTRUCTIONS,
            memory = MEMORY,
            target = platformTarget(),
        )

        return compiled.getOrPut(CompileKey(ByteBuffer.wrap(bytes.copyOf()), config, options)) {
            when (val validated = ScriptModules.validate(bytes, config)) {
                is ChasmResult.Error -> ChasmResult.Error(ScriptFailure.Module(validated.error))
                is ChasmResult.Success -> try {
                    lower(validated.result, options.copy(entryClass = "$ENTRY_PACKAGE.Module${modules.incrementAndGet()}"))
                } catch (failure: IllegalStateException) {
                    ChasmResult.Error(ScriptFailure.Execution(ExecutionError.Unsupported(failure.message ?: failure.toString())))
                }
            }
        }
    }

    private fun lower(
        module: WasmModule,
        options: JvmOptions,
    ): ChasmResult<Module, ScriptFailure> {

        val cir = ModuleCompiler(
            module = module,
            types = module.typeResolver(),
            recordProvenance = false,
            devirtualiseIndirectCalls = !options.stacktraces && !options.meterFuel && !options.interruptible,
        ).fold({ it }) { error -> throw IllegalStateException("cannot compile: $error") }

        val generated = GeneratedClassFiles()
        val lowered = try {
            ModuleLowerer(module, cir, options, generated, ModuleDeclarationsFactory(module))
        } catch (unsupported: UnsupportedFunctions) {
            throw IllegalStateException("AOT: ${unsupported.message}")
        }

        return when (val loaded = loadClassFiles(generated.classFiles, generated.resources, lowered.moduleClass)) {
            is ChasmResult.Success -> loaded
            is ChasmResult.Error -> ChasmResult.Error(ScriptFailure.Module(loaded.error))
        }
    }
}

private data class CompileKey(
    val bytes: ByteBuffer,
    val config: ModuleConfig,
    val options: JvmOptions,
)

private class GeneratedClassFiles : JvmOutput {

    val classFiles = HashMap<String, ByteArray>()
    val resources = HashMap<String, ByteArray>()

    override fun classFile(
        internalName: String,
        bytes: ByteArray,
    ) {
        check(classFiles.put(internalName.replace('/', '.'), bytes) == null) { "class $internalName generated twice" }
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
}
