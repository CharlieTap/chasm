package io.github.charlietap.chasm.jit.internal

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.compiler.frontend.ParallelModuleCompiler
import io.github.charlietap.chasm.compiler.frontend.UncompilableFunctions
import io.github.charlietap.chasm.declarations.ModuleTypeResolver
import io.github.charlietap.chasm.jit.internal.CompileError.Stage
import io.github.charlietap.chasm.parallel.ParallelTaskExecutor
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.coroutineContext
import io.github.charlietap.chasm.embedding.shapes.Module as EmbeddingModule

/** Compiles [module] to the platform's own code: DEX on Android, JVM classes elsewhere. */
@Suppress("TooGenericExceptionCaught")
internal suspend fun compile(
    module: EmbeddingModule,
    config: CompileConfig,
): CompileResult = try {
    compileUnchecked(module, config)
} catch (cancelled: CancellationException) {
    throw cancelled
} catch (failure: RuntimeException) {
    error(Stage.GENERATION, failure.message ?: failure.toString())
} catch (overflow: StackOverflowError) {
    error(Stage.GENERATION, "ran out of stack compiling: $overflow")
}

/** Compiles [module] for this platform, leaving [compile] to turn what it throws into an error. */
internal expect suspend fun compileUnchecked(
    module: EmbeddingModule,
    config: CompileConfig,
): CompileResult

/** The module's functions in Chasm's IR, or which of them cannot be compiled and why. */
internal suspend inline fun frontend(
    ast: Module,
    types: ModuleTypeResolver,
    executor: ParallelTaskExecutor,
    crossinline moduleCompiler: ParallelModuleCompiler,
    crossinline uncompilableFunctions: UncompilableFunctions,
): Result<CirModule, CompileResult.Error> {
    val code = moduleCompiler(ast, types, executor).getOrElse { failure ->
        coroutineContext.ensureActive()
        val functions = uncompilableFunctions(ast, types)
        coroutineContext.ensureActive()
        val reasons = functions.entries.joinToString { (index, reason) -> "$index ($reason)" }
        return Err(error(Stage.GENERATION, "cannot compile functions: ${reasons.ifEmpty { failure.toString() }}", functions.keys.toList()))
    }
    return Ok(code)
}

/** Runs [load], turning compiled code the platform will not load into a [Stage.LOADING] error. */
@Suppress("TooGenericExceptionCaught")
internal inline fun loading(
    load: () -> JitCompilation,
): CompileResult = try {
    CompileResult.Success(load())
} catch (failure: ReflectiveOperationException) {
    error(Stage.LOADING, failure.toString())
} catch (failure: LinkageError) {
    error(Stage.LOADING, failure.toString())
} catch (cancelled: CancellationException) {
    throw cancelled
} catch (failure: RuntimeException) {
    error(Stage.LOADING, failure.toString())
}
