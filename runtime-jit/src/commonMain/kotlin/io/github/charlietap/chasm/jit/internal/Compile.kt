@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.jit.internal

import com.github.michaelbull.result.getOrElse
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.ast.module.typeResolver
import io.github.charlietap.chasm.compiler.backend.dex.DexOptions
import io.github.charlietap.chasm.compiler.backend.dex.ParallelModuleLowerer
import io.github.charlietap.chasm.compiler.backend.dex.UnsupportedFunctions
import io.github.charlietap.chasm.compiler.frontend.ParallelModuleCompiler
import io.github.charlietap.chasm.compiler.frontend.UncompilableFunctions
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes._binary
import io.github.charlietap.chasm.embedding.shapes._module
import io.github.charlietap.chasm.jit.internal.CompileError.Stage
import io.github.charlietap.chasm.parallel.ParallelTaskExecutor
import io.github.charlietap.chasm.runtime.compiled.CodeInstallation
import io.github.charlietap.chasm.runtime.compiled.byteArrayBase
import kotlinx.coroutines.ensureActive
import java.util.function.Consumer
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.coroutineContext

internal suspend fun compile(
    module: Module,
    config: CompileConfig,
): CompileResult {
    return compile(
        module = module,
        config = config,
        executor = compileExecutor,
        packageNames = packageNames::next,
        moduleCompiler = { ast, types, executor ->
            ParallelModuleCompiler(
                module = ast,
                types = types,
                taskExecutor = executor,
                devirtualiseIndirectCalls = !config.stacktraces && !config.meterFuel && !config.interruptible,
            )
        },
        uncompilableFunctions = ::UncompilableFunctions,
        moduleLowerer = ::ParallelModuleLowerer,
        dexLoader = ::DexLoader,
        codeClassInitializer = ::CodeClassInitializer,
    )
}

internal suspend inline fun compile(
    module: Module,
    config: CompileConfig,
    executor: ParallelTaskExecutor,
    crossinline packageNames: (ByteArray?, DexOptions) -> String,
    crossinline moduleCompiler: ParallelModuleCompiler,
    crossinline uncompilableFunctions: UncompilableFunctions,
    crossinline moduleLowerer: ParallelModuleLowerer,
    crossinline dexLoader: DexLoader,
    crossinline codeClassInitializer: CodeClassInitializer,
): CompileResult {
    return try {
        compileUnchecked(module, config, executor, packageNames, moduleCompiler, uncompilableFunctions, moduleLowerer, dexLoader, codeClassInitializer)
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (failure: RuntimeException) {
        error(Stage.GENERATION, failure.message ?: failure.toString())
    }
}

internal suspend inline fun compileUnchecked(
    module: Module,
    config: CompileConfig,
    executor: ParallelTaskExecutor,
    crossinline packageNames: (ByteArray?, DexOptions) -> String,
    crossinline moduleCompiler: ParallelModuleCompiler,
    crossinline uncompilableFunctions: UncompilableFunctions,
    crossinline moduleLowerer: ParallelModuleLowerer,
    crossinline dexLoader: DexLoader,
    crossinline codeClassInitializer: CodeClassInitializer,
): CompileResult {

    coroutineContext.ensureActive()
    val ast = module._module()
    val types = ast.typeResolver()
    val code = moduleCompiler(ast, types, executor).getOrElse { failure ->
        coroutineContext.ensureActive()
        val functions = uncompilableFunctions(ast, types)
        coroutineContext.ensureActive()
        val reasons = functions.entries.joinToString { (index, reason) -> "$index ($reason)" }
        return error(Stage.GENERATION, "cannot compile functions: ${reasons.ifEmpty { failure.toString() }}", functions.keys.toList())
    }
    coroutineContext.ensureActive()

    val emission = DexOptions(
        stacktraces = config.stacktraces,
        meterFuel = config.meterFuel,
        interruptible = config.interruptible,
        byteArrayBaseOffset = byteArrayBase().takeIf { base -> base >= 0 },
    )
    val packageName = packageNames(module._binary(), emission)
    val options = emission.copy(packageName = packageName)
    val dex = try {
        moduleLowerer(ast, code, options, executor)
    } catch (unsupported: UnsupportedFunctions) {
        return error(Stage.GENERATION, unsupported.message.orEmpty(), unsupported.reasons.keys.toList())
    }
    coroutineContext.ensureActive()

    val installer = try {
        val loader = dexLoader(dex.dex, CodeInstallation::class.java.classLoader!!)
        codeClassInitializer(loader, dex.classesToInitialize, executor)
        @Suppress("UNCHECKED_CAST")
        loader.loadClass(dex.installer).getDeclaredConstructor().newInstance() as Consumer<CodeInstallation>
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

    return CompileResult.Success(JitCompilation(packageName, dex.chunkStarts, installer, dex.inlineMemory))
}

private val packageNames = PackageNames()

internal fun error(
    stage: Stage,
    message: String,
    functions: List<Int> = emptyList(),
): CompileResult.Error = CompileResult.Error(CompileError(message, stage, functions))
