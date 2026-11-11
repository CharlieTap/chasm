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
import io.github.charlietap.chasm.runtime.compiled.byteArrayBase
import java.nio.ByteBuffer
import java.util.WeakHashMap
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
        packageNames = packageNames(store)::next,
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
    // The package name digests the module and the options, so it serves as the key for the compiled code.
    val activeCache = cache?.takeIf { PackageNames.hashed(packageName) }
    val cached = activeCache?.read(packageName)?.let { bytes -> decodeCompilation(bytes, packageName) }
    if (cached != null) {
        val loaded = load(packageName, cached.dex, cached.installer, cached.chunkStarts, cached.inlineMemory, dexLoader)
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
    val loaded = load(packageName, dex.dex.map(ByteBuffer::wrap), dex.installer, dex.chunkStarts, dex.inlineMemory, dexLoader)
    if (loaded is CompileResult.Success) activeCache?.write(packageName, encodeCompilation(packageName, dex))
    return loaded
}

internal inline fun load(
    packageName: String,
    dex: List<ByteBuffer>,
    installerName: String,
    chunkStarts: Map<Int, IntArray>,
    inlineMemory: Boolean,
    crossinline dexLoader: DexLoader,
): CompileResult {
    val installer = try {
        val loader = dexLoader(dex, CodeInstallation::class.java.classLoader!!)
        @Suppress("UNCHECKED_CAST")
        loader.loadClass(installerName).getDeclaredConstructor().newInstance() as Consumer<CodeInstallation>
    } catch (failure: ReflectiveOperationException) {
        return error(Stage.LOADING, failure.toString())
    } catch (failure: LinkageError) {
        return error(Stage.LOADING, failure.toString())
    } catch (failure: RuntimeException) {
        return error(Stage.LOADING, failure.toString())
    }
    return CompileResult.Success(JitCompilation(packageName, chunkStarts, installer, inlineMemory))
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

// Two instances of a module only need distinct package names when they share a store, the scope in which
// compiled stack traces are resolved. Naming per store lets a module in a new store reuse its cache entry.
private val storePackageNames = WeakHashMap<RuntimeStore, PackageNames>()

private fun packageNames(
    store: RuntimeStore,
): PackageNames = synchronized(storePackageNames) { storePackageNames.getOrPut(store, ::PackageNames) }

internal fun error(
    stage: Stage,
    message: String,
    functions: List<Int> = emptyList(),
): CompileResult.Error = CompileResult.Error(CompileError(message, stage, functions))
