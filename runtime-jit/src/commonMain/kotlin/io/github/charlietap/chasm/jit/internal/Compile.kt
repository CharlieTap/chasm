@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.jit.internal

import com.github.michaelbull.result.getOrElse
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.ast.module.typeResolver
import io.github.charlietap.chasm.compiler.backend.dex.DexOptions
import io.github.charlietap.chasm.compiler.backend.dex.ModuleLowerer
import io.github.charlietap.chasm.compiler.backend.dex.UnsupportedFunctions
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.chasm.compiler.frontend.UncompilableFunctions
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes._binary
import io.github.charlietap.chasm.embedding.shapes._module
import io.github.charlietap.chasm.jit.internal.CompileError.Stage
import io.github.charlietap.chasm.runtime.compiled.CodeInstallation
import io.github.charlietap.chasm.runtime.compiled.ModuleCompiledFrames
import io.github.charlietap.chasm.runtime.compiled.byteArrayBase
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import java.util.function.Consumer
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

internal fun compile(
    module: Module,
    config: CompileConfig,
    store: RuntimeStore,
    cache: CompilationCache?,
): CompileResult =
    compile(
        module = module,
        config = config,
        cache = cache,
        packageNames = { bytes, options -> packageName(bytes, options, packageNamesIn(store)) },
        moduleCompiler = { ast, types -> ModuleCompiler(ast, types) },
        uncompilableFunctions = ::UncompilableFunctions,
        moduleLowerer = ::ModuleLowerer,
        dexLoader = ::DexLoader,
    )

internal inline fun compile(
    module: Module,
    config: CompileConfig,
    cache: CompilationCache?,
    crossinline packageNames: (ByteArray?, DexOptions) -> String,
    crossinline moduleCompiler: ModuleCompiler,
    crossinline uncompilableFunctions: UncompilableFunctions,
    crossinline moduleLowerer: ModuleLowerer,
    crossinline dexLoader: DexLoader,
): CompileResult = try {
    compileUnchecked(module, config, cache, packageNames, moduleCompiler, uncompilableFunctions, moduleLowerer, dexLoader)
} catch (failure: RuntimeException) {
    error(Stage.GENERATION, failure.message ?: failure.toString())
}

internal inline fun compileUnchecked(
    module: Module,
    config: CompileConfig,
    cache: CompilationCache?,
    crossinline packageNames: (ByteArray?, DexOptions) -> String,
    crossinline moduleCompiler: ModuleCompiler,
    crossinline uncompilableFunctions: UncompilableFunctions,
    crossinline moduleLowerer: ModuleLowerer,
    crossinline dexLoader: DexLoader,
): CompileResult {
    val emission = DexOptions(
        stacktraces = config.stacktraces,
        meterFuel = config.meterFuel,
        interruptible = config.interruptible,
        byteArrayBaseOffset = byteArrayBase().takeIf { base -> base >= 0 },
    )
    val packageName = packageNames(module._binary(), emission)
    // A module with bytes has a package name that identifies its compiled code, so it is the cache key.
    val activeCache = cache?.takeIf { module._binary() != null }
    val cached = activeCache?.read(packageName)?.let { bytes -> decodeCompilation(bytes, packageName) }
    if (cached != null) {
        val loaded = load(packageName, cached, dexLoader)
        if (loaded is CompileResult.Success) return loaded
    }
    val ast = module._module()
    val types = ast.typeResolver()
    val code = moduleCompiler(ast, types).getOrElse { failure ->
        val functions = uncompilableFunctions(ast, types)
        val reasons = functions.entries.joinToString { (index, reason) -> "$index ($reason)" }
        return error(Stage.GENERATION, "cannot compile functions: ${reasons.ifEmpty { failure.toString() }}", functions.keys.toList())
    }
    val options = emission.copy(packageName = packageName)
    val dex = try {
        moduleLowerer(ast, code, options)
    } catch (unsupported: UnsupportedFunctions) {
        return error(Stage.GENERATION, unsupported.message.orEmpty(), unsupported.reasons.keys.toList())
    }
    val loaded = load(packageName, dex.toCachedCompilation(), dexLoader)
    if (loaded is CompileResult.Success) activeCache?.write(packageName, encodeCompilation(packageName, dex))
    return loaded
}

internal inline fun load(
    packageName: String,
    compilation: CachedCompilation,
    crossinline dexLoader: DexLoader,
): CompileResult {
    val installer = try {
        val loader = dexLoader(compilation.dex, CodeInstallation::class.java.classLoader!!)
        @Suppress("UNCHECKED_CAST")
        loader.loadClass(compilation.installer).getDeclaredConstructor().newInstance() as Consumer<CodeInstallation>
    } catch (failure: ReflectiveOperationException) {
        return error(Stage.LOADING, failure.toString())
    } catch (failure: LinkageError) {
        return error(Stage.LOADING, failure.toString())
    } catch (failure: RuntimeException) {
        return error(Stage.LOADING, failure.toString())
    }
    return CompileResult.Success(JitCompilation(packageName, compilation.chunkStarts, installer, compilation.inlineMemory))
}

// A cache is the host's code: if it fails, the module is compiled as though the cache were absent.
@Suppress("TooGenericExceptionCaught", "SwallowedException")
internal fun CompilationCache.read(
    key: String,
): ByteArray? = try {
    get(key)
} catch (_: Exception) {
    null
}

@Suppress("TooGenericExceptionCaught", "SwallowedException")
internal fun CompilationCache.write(
    key: String,
    value: ByteArray,
) {
    try {
        put(key, value)
    } catch (_: Exception) {
    }
}

private fun packageNamesIn(
    store: RuntimeStore,
): Set<String> = store.functions.mapNotNullTo(HashSet()) { function ->
    ((function as? FunctionInstance.WasmFunction)?.module?.compiledFrames as? ModuleCompiledFrames)?.packageName
}

internal fun error(
    stage: Stage,
    message: String,
    functions: List<Int> = emptyList(),
): CompileResult.Error = CompileResult.Error(CompileError(message, stage, functions))
