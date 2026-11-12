@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.jit.internal

import com.github.michaelbull.result.getOrElse
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.ast.module.typeResolver
import io.github.charlietap.chasm.compiler.backend.dex.DexOptions
import io.github.charlietap.chasm.compiler.backend.dex.ModuleLowerer
import io.github.charlietap.chasm.compiler.backend.dex.UnsupportedFunctions
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.chasm.compiler.frontend.ModuleRewriter
import io.github.charlietap.chasm.compiler.frontend.UncompilableFunctions
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes._binary
import io.github.charlietap.chasm.embedding.shapes._module
import io.github.charlietap.chasm.jit.internal.CompileError.Stage
import io.github.charlietap.chasm.runtime.compiled.CodeInstallation
import io.github.charlietap.chasm.runtime.compiled.byteArrayBase
import java.util.function.Consumer

internal fun compile(
    module: Module,
    config: CompileConfig,
): CompileResult =
    compile(
        module = module,
        config = config,
        packageNames = packageNames::next,
        moduleRewriter = { source, types ->
            ModuleRewriter(
                module = source,
                types = types,
                preserveSource = config.stacktraces || config.meterFuel || config.interruptible,
            )
        },
        moduleCompiler = ::ModuleCompiler,
        uncompilableFunctions = ::UncompilableFunctions,
        moduleLowerer = ::ModuleLowerer,
        dexLoader = ::DexLoader,
    )

internal inline fun compile(
    module: Module,
    config: CompileConfig,
    crossinline packageNames: (ByteArray?, DexOptions) -> String,
    crossinline moduleRewriter: ModuleRewriter,
    crossinline moduleCompiler: ModuleCompiler,
    crossinline uncompilableFunctions: UncompilableFunctions,
    crossinline moduleLowerer: ModuleLowerer,
    crossinline dexLoader: DexLoader,
): CompileResult = try {
    compileUnchecked(module, config, packageNames, moduleRewriter, moduleCompiler, uncompilableFunctions, moduleLowerer, dexLoader)
} catch (failure: RuntimeException) {
    error(Stage.GENERATION, failure.message ?: failure.toString())
}

internal inline fun compileUnchecked(
    module: Module,
    config: CompileConfig,
    crossinline packageNames: (ByteArray?, DexOptions) -> String,
    crossinline moduleRewriter: ModuleRewriter,
    crossinline moduleCompiler: ModuleCompiler,
    crossinline uncompilableFunctions: UncompilableFunctions,
    crossinline moduleLowerer: ModuleLowerer,
    crossinline dexLoader: DexLoader,
): CompileResult {
    val source = module._module()
    val types = source.typeResolver()
    val ast = moduleRewriter(source, types)
    val code = moduleCompiler(ast, types).getOrElse { failure ->
        val functions = uncompilableFunctions(ast, types)
        val reasons = functions.entries.joinToString { (index, reason) -> "$index ($reason)" }
        return error(Stage.GENERATION, "cannot compile functions: ${reasons.ifEmpty { failure.toString() }}", functions.keys.toList())
    }
    val emission = DexOptions(
        stacktraces = config.stacktraces,
        meterFuel = config.meterFuel,
        interruptible = config.interruptible,
        byteArrayBaseOffset = byteArrayBase().takeIf { base -> base >= 0 },
    )
    val packageName = packageNames(module._binary(), emission)
    val options = emission.copy(packageName = packageName)
    val dex = try {
        moduleLowerer(ast, code, options)
    } catch (unsupported: UnsupportedFunctions) {
        return error(Stage.GENERATION, unsupported.message.orEmpty(), unsupported.reasons.keys.toList())
    }
    val installer = try {
        val loader = dexLoader(dex.dex, CodeInstallation::class.java.classLoader!!)
        @Suppress("UNCHECKED_CAST")
        loader.loadClass(dex.installer).getDeclaredConstructor().newInstance() as Consumer<CodeInstallation>
    } catch (failure: ReflectiveOperationException) {
        return error(Stage.LOADING, failure.toString())
    } catch (failure: LinkageError) {
        return error(Stage.LOADING, failure.toString())
    } catch (failure: RuntimeException) {
        return error(Stage.LOADING, failure.toString())
    }
    return CompileResult.Success(JitCompilation(packageName, dex.chunkStarts, installer, dex.inlineMemory))
}

private val packageNames = PackageNames()

internal fun error(
    stage: Stage,
    message: String,
    functions: List<Int> = emptyList(),
): CompileResult.Error = CompileResult.Error(CompileError(message, stage, functions))
