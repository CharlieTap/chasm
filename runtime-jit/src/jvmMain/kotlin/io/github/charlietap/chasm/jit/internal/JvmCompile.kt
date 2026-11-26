@file:OptIn(InternalChasmApi::class, ExperimentalChasmApi::class)

package io.github.charlietap.chasm.jit.internal

import com.github.michaelbull.result.getOrElse
import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.aot.CompiledModule
import io.github.charlietap.chasm.aot.internal.MemoryClassLoader
import io.github.charlietap.chasm.ast.module.typeResolver
import io.github.charlietap.chasm.compiler.backend.jvm.JvmOptions
import io.github.charlietap.chasm.compiler.backend.jvm.JvmOutput
import io.github.charlietap.chasm.compiler.backend.jvm.MemoryLowering
import io.github.charlietap.chasm.compiler.backend.jvm.ModuleLowerer
import io.github.charlietap.chasm.compiler.backend.jvm.UnsupportedFunctions
import io.github.charlietap.chasm.compiler.frontend.ParallelModuleCompiler
import io.github.charlietap.chasm.compiler.frontend.UncompilableFunctions
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes._declarations
import io.github.charlietap.chasm.embedding.shapes._module
import io.github.charlietap.chasm.jit.internal.CompileError.Stage
import io.github.charlietap.chasm.runtime.compiled.CodeInstallation
import kotlinx.coroutines.ensureActive
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import java.util.function.Consumer
import kotlin.coroutines.coroutineContext

// Lowers to JVM classes with the backend chasm-aot uses, defines them in a class loader of the module's own, and
// installs them as the DEX path does. HotSpot then compiles them as it would any other classes.
internal actual suspend fun compileUnchecked(
    module: Module,
    config: CompileConfig,
): CompileResult {
    val context = coroutineContext
    context.ensureActive()
    val ast = module._module()
    val code = frontend(ast, ast.typeResolver(), compileExecutor, { ast, types, executor ->
        ParallelModuleCompiler(ast, types, executor, devirtualiseIndirectCalls = config.devirtualiseIndirectCalls)
    }, ::UncompilableFunctions).getOrElse { refused -> return refused }
    context.ensureActive()

    val packageName = PACKAGE_PREFIX + modules.incrementAndGet()
    val options = JvmOptions(
        packageName = packageName,
        storeConfig = StoreConfig(meterFuel = config.meterFuel, interruptible = config.interruptible),
        memory = MemoryLowering.BYTE_BUFFER,
        stacktraces = config.stacktraces,
    )
    val output = MemoryOutput()
    val lowered = try {
        ModuleLowerer(ast, code, options, output, module._declarations(), emptyMap(), compileWorkers) { context.ensureActive() }
    } catch (unsupported: UnsupportedFunctions) {
        return error(Stage.GENERATION, unsupported.message.orEmpty(), unsupported.reasons.keys.toList())
    }
    context.ensureActive()

    return loading {
        val names = output.classes.keys.map { name -> name.replace('/', '.') }
        val loader = MemoryClassLoader(output.classes, output.resources, CompiledModule::class.java.classLoader)
        CodeClassInitializer(loader, names, compileExecutor)
        val compiled = loader.module(lowered.moduleClass)
        context.ensureActive()
        val install = Consumer<CodeInstallation> { installation ->
            if (lowered.inlineMemory == MemoryLowering.BYTE_BUFFER) {
                require(installation.memory(0).byteBuffer != null) {
                    "memory 0 is not backed by a ByteBuffer the compiled code can access"
                }
            }
            compiled.code.install(installation)
        }
        JitCompilation(packageName, lowered.chunkStarts, install, inlineMemory = false)
    }
}

/** Holds what the backend writes, which lowers functions on several threads at once. */
private class MemoryOutput : JvmOutput {

    val classes = ConcurrentHashMap<String, ByteArray>()
    val resources = ConcurrentHashMap<String, ByteArray>()

    override fun classFile(
        internalName: String,
        bytes: ByteArray,
    ) {
        classes[internalName] = bytes
    }

    override fun <T> resource(
        path: String,
        write: (OutputStream) -> T,
    ): T {
        val stream = ByteArrayOutputStream()
        return write(stream).also { resources[path] = stream.toByteArray() }
    }
}

private val modules = AtomicLong()

private const val PACKAGE_PREFIX = "chasm.jit.m"
