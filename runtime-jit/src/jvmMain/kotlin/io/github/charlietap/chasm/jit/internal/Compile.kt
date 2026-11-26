@file:OptIn(InternalChasmApi::class, ExperimentalChasmApi::class)

package io.github.charlietap.chasm.jit.internal

import com.github.michaelbull.result.getOrElse
import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.aot.CompiledModule
import io.github.charlietap.chasm.ast.module.typeResolver
import io.github.charlietap.chasm.compiler.backend.jvm.ClassTooLarge
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
import kotlinx.coroutines.ensureActive
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import java.util.function.Consumer
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.coroutineContext

// Lowers to JVM classes with the backend chasm-aot uses, defines them in a class loader of the module's own, and
// installs them as the DEX path does. HotSpot then compiles them as it would any other classes.
@Suppress("TooGenericExceptionCaught")
internal actual suspend fun compile(
    module: Module,
    config: CompileConfig,
): CompileResult = try {
    compileUnchecked(module, config)
} catch (cancelled: CancellationException) {
    throw cancelled
} catch (failure: RuntimeException) {
    error(Stage.GENERATION, failure.message ?: failure.toString())
}

private suspend fun compileUnchecked(
    module: Module,
    config: CompileConfig,
): CompileResult {
    coroutineContext.ensureActive()
    val ast = module._module()
    val types = ast.typeResolver()
    val code = ParallelModuleCompiler(
        module = ast,
        types = types,
        taskExecutor = compileExecutor,
        devirtualiseIndirectCalls = !config.stacktraces && !config.meterFuel && !config.interruptible,
    ).getOrElse { failure ->
        val functions = UncompilableFunctions(ast, types)
        val reasons = functions.entries.joinToString { (index, reason) -> "$index ($reason)" }
        return error(Stage.GENERATION, "cannot compile functions: ${reasons.ifEmpty { failure.toString() }}", functions.keys.toList())
    }
    coroutineContext.ensureActive()

    val packageName = PACKAGE_PREFIX + modules.incrementAndGet()
    val options = JvmOptions(
        packageName = packageName,
        storeConfig = StoreConfig(meterFuel = config.meterFuel, interruptible = config.interruptible),
        memory = MemoryLowering.BYTE_BUFFER,
        stacktraces = config.stacktraces,
    )
    val output = MemoryOutput()
    val lowered = try {
        ModuleLowerer(ast, code, options, output, module._declarations())
    } catch (unsupported: UnsupportedFunctions) {
        return error(Stage.GENERATION, unsupported.message.orEmpty(), unsupported.reasons.keys.toList())
    } catch (tooLarge: ClassTooLarge) {
        return error(Stage.GENERATION, tooLarge.message.orEmpty())
    }
    coroutineContext.ensureActive()

    val compiled = try {
        val loader = MemoryClassLoader(output.classes, output.resources, CompiledModule::class.java.classLoader!!)
        CodeClassInitializer(loader, output.classes.keys.map { name -> name.replace('/', '.') }, compileExecutor)
        loader.loadClass(lowered.moduleClass).getField(INSTANCE_FIELD).get(null) as CompiledModule
    } catch (failure: ReflectiveOperationException) {
        return error(Stage.LOADING, failure.toString())
    } catch (failure: LinkageError) {
        return error(Stage.LOADING, failure.toString())
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (failure: RuntimeException) {
        return error(Stage.LOADING, failure.toString())
    }
    coroutineContext.ensureActive()

    return CompileResult.Success(JitCompilation(packageName, emptyMap(), Consumer(compiled.code::install), inlineMemory = false))
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

/** Defines the module's classes before asking its parent, so a class of the same name elsewhere never stands in. */
private class MemoryClassLoader(
    private val classes: Map<String, ByteArray>,
    private val resources: Map<String, ByteArray>,
    parent: ClassLoader,
) : ClassLoader(parent) {

    override fun loadClass(
        name: String,
        resolve: Boolean,
    ): Class<*> = synchronized(getClassLoadingLock(name)) {
        val bytes = classes[name.replace('.', '/')] ?: return super.loadClass(name, resolve)
        val loaded = findLoadedClass(name) ?: defineClass(name, bytes, 0, bytes.size)
        if (resolve) resolveClass(loaded)
        loaded
    }

    override fun getResourceAsStream(name: String): java.io.InputStream? =
        resources[name]?.inputStream() ?: super.getResourceAsStream(name)
}

private val modules = AtomicLong()

private const val PACKAGE_PREFIX = "chasm.jit.m"

private const val INSTANCE_FIELD = "INSTANCE"
