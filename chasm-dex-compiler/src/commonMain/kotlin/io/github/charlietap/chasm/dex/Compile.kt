@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.dex

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.ast.module.typeResolver
import io.github.charlietap.chasm.compiled.format.COMPILED_RUNTIME_ABI
import io.github.charlietap.chasm.compiled.format.dexFileName
import io.github.charlietap.chasm.compiled.format.trapSitesResource
import io.github.charlietap.chasm.compiler.backend.analysis.UnsupportedFunctions
import io.github.charlietap.chasm.compiler.backend.dex.DexOptions
import io.github.charlietap.chasm.compiler.backend.dex.DexTooLarge
import io.github.charlietap.chasm.compiler.backend.dex.ParallelModuleLowerer
import io.github.charlietap.chasm.compiler.backend.dex.UnsupportedModule
import io.github.charlietap.chasm.compiler.backend.dex.abi.ART_BYTE_ARRAY_BASE_OFFSET
import io.github.charlietap.chasm.compiler.driver.ClassNames
import io.github.charlietap.chasm.compiler.driver.ModuleSources
import io.github.charlietap.chasm.compiler.driver.SourceFailure
import io.github.charlietap.chasm.compiler.driver.SourceModule
import io.github.charlietap.chasm.compiler.driver.TrapSiteSource
import io.github.charlietap.chasm.compiler.frontend.UncompilableFunctions
import io.github.charlietap.chasm.compiler.frontend.diagnostic.TrapSiteTables
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.declarations.moduleInfo
import io.github.charlietap.chasm.decoder.WasmFunctionBodyOffsets
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.dex.CompileError.Stage
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.module.ModuleDeclarationsFactory
import io.github.charlietap.chasm.parallel.ParallelTaskExecutor
import io.github.charlietap.chasm.validator.WasmModuleValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.nio.file.Path
import kotlin.jvm.JvmInline

suspend fun compile(
    input: ByteArray,
    output: DexOutput,
    config: CompileConfig,
): ChasmResult<CompiledModuleInfo, CompileError> {
    return withContext(Dispatchers.Default) {
        dexCompilation().compile(Input.Bytes(input), output, config)
    }
}

suspend fun compile(
    input: Path,
    output: DexOutput,
    config: CompileConfig,
): ChasmResult<CompiledModuleInfo, CompileError> {
    return withContext(Dispatchers.Default) {
        dexCompilation().compile(Input.File(input), output, config)
    }
}

internal sealed interface Input {

    @JvmInline
    value class Bytes(val bytes: ByteArray) : Input

    @JvmInline
    value class File(val path: Path) : Input
}

internal fun dexCompilation(): DexCompilation {
    return DexCompilation(
        sources = ModuleSources(::WasmModuleDecoder, ::WasmModuleDecoder, ::WasmModuleValidator),
        classNames = ClassNames(),
        frontend = ::DexFrontend,
        uncompilableFunctions = ::UncompilableFunctions,
        declarationsFactory = ::ModuleDeclarationsFactory,
        trapSites = TrapSiteSource(::WasmFunctionBodyOffsets, ::TrapSiteTables),
        lowerer = ::ParallelModuleLowerer,
        executor = compileExecutor,
    )
}

internal class DexCompilation(
    private val sources: ModuleSources,
    private val classNames: ClassNames,
    private val frontend: DexFrontend,
    private val uncompilableFunctions: UncompilableFunctions,
    private val declarationsFactory: ModuleDeclarationsFactory,
    private val trapSites: TrapSiteSource,
    private val lowerer: ParallelModuleLowerer,
    private val executor: ParallelTaskExecutor,
) {

    suspend fun compile(
        input: Input,
        output: DexOutput,
        config: CompileConfig,
    ): ChasmResult<CompiledModuleInfo, CompileError> {

        if (!classNames.valid(config.className)) {
            return error(Stage.CONFIGURATION, "\"${config.className}\" is not a Java class name")
        }

        val runtime = config.runtime
        val source = read(input, runtime).getOrElse { failure -> return error(stage(failure), failure.message) }
        currentCoroutineContext().ensureActive()

        val module = source.module
        val types = module.typeResolver()
        val code = frontend(module, types, runtime, executor).getOrElse { failure ->
            currentCoroutineContext().ensureActive()
            val functions = uncompilableFunctions(module, types).mapValues { (_, reason) -> reason.toString() }
            val reasons = functionList(functions).ifEmpty { failure.toString() }
            return error(Stage.GENERATION, "cannot compile functions: $reasons", functions.keys.toList())
        }
        currentCoroutineContext().ensureActive()

        val sites = if (runtime.stacktraces) {
            trapSites.sites(source, code)
        } else {
            emptyMap()
        }
        val declarations = declarationsFactory(module)
        val staged = StagedDexOutput(output)
        val options = DexOptions(
            entryClass = config.className,
            runtime = runtime,
            byteArrayBaseOffset = ART_BYTE_ARRAY_BASE_OFFSET,
        )

        val dex = try {
            lowerer(module, code, options, declarations, sites, staged, executor)
        } catch (unsupported: UnsupportedFunctions) {
            val reasons = unsupported.reasons
            return error(Stage.GENERATION, "cannot generate functions: ${functionList(reasons)}", reasons.keys.toList())
        } catch (unsupported: UnsupportedModule) {
            return error(Stage.GENERATION, unsupported.message.orEmpty())
        } catch (tooLarge: DexTooLarge) {
            return error(Stage.GENERATION, tooLarge.message.orEmpty())
        }
        currentCoroutineContext().ensureActive()

        val files = dex.dex.mapIndexed { index, bytes -> staged.dexFile(dexFileName(index), bytes) }
        staged.commit().getOrElse { failure -> return error(Stage.OUTPUT, "cannot write the compiled module: ${failure.message}") }

        val info = moduleInfo(declarations)

        return ChasmResult.Success(
            CompiledModuleInfo(
                config = config,
                imports = info.imports,
                exports = info.exports,
                runtimeAbi = COMPILED_RUNTIME_ABI,
                compilerVersion = BuildConfig.VERSION,
                sourceHash = source.hash,
                diagnostics = if (runtime.stacktraces) {
                    Diagnostics(trapSitesResource(config.className.replace('.', '/')))
                } else {
                    null
                },
                classes = dex.classes,
                dexFiles = files,
                resources = staged.resources,
            ),
        )
    }

    private fun read(
        input: Input,
        runtime: RuntimeConfig,
    ): Result<SourceModule, SourceFailure> {
        return when (input) {
            is Input.Bytes -> sources.read(input.bytes, runtime.limits.memory)
            is Input.File -> sources.read(input.path, runtime.stacktraces, runtime.limits.memory)
        }
    }

    private fun stage(failure: SourceFailure): Stage {
        return when (failure) {
            is SourceFailure.Unreadable -> Stage.INPUT
            is SourceFailure.Undecodable -> Stage.DECODE
            is SourceFailure.Invalid -> Stage.VALIDATION
            is SourceFailure.OverLimit -> Stage.CONFIGURATION
            is SourceFailure.Unsupported -> Stage.GENERATION
        }
    }

    private fun functionList(reasons: Map<Int, String>): String {

        val listed = reasons.entries.take(LISTED_FUNCTIONS).joinToString { (index, reason) -> "$index ($reason)" }
        val remaining = reasons.size - LISTED_FUNCTIONS

        return if (remaining > 0) "$listed and $remaining more" else listed
    }

    private fun error(
        stage: Stage,
        message: String,
        functions: List<Int> = emptyList(),
    ): ChasmResult.Error<CompileError> {
        return ChasmResult.Error(CompileError(message, stage, functions))
    }

    private companion object {
        const val LISTED_FUNCTIONS = 10
    }
}
