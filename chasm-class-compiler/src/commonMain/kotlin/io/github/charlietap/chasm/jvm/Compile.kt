@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.jvm

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.ast.module.typeResolver
import io.github.charlietap.chasm.compiled.format.ArtifactTarget
import io.github.charlietap.chasm.compiled.format.COMPILED_RUNTIME_ABI
import io.github.charlietap.chasm.compiled.format.trapLinesResource
import io.github.charlietap.chasm.compiled.format.trapSitesResource
import io.github.charlietap.chasm.compiler.backend.analysis.UnsupportedFunctions
import io.github.charlietap.chasm.compiler.backend.jvm.ClassTooLarge
import io.github.charlietap.chasm.compiler.backend.jvm.JvmOptions
import io.github.charlietap.chasm.compiler.backend.jvm.JvmOutput
import io.github.charlietap.chasm.compiler.backend.jvm.MemoryLowering
import io.github.charlietap.chasm.compiler.backend.jvm.ModuleLowerer
import io.github.charlietap.chasm.compiler.backend.jvm.size.BytecodeSize
import io.github.charlietap.chasm.compiler.backend.jvm.size.DexUnitEstimate
import io.github.charlietap.chasm.compiler.driver.ClassNames
import io.github.charlietap.chasm.compiler.driver.ModuleSources
import io.github.charlietap.chasm.compiler.driver.SourceFailure
import io.github.charlietap.chasm.compiler.driver.SourceModule
import io.github.charlietap.chasm.compiler.driver.TrapSiteSource
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.chasm.compiler.frontend.UncompilableFunctions
import io.github.charlietap.chasm.compiler.frontend.diagnostic.TrapSiteTables
import io.github.charlietap.chasm.config.CpuLimits
import io.github.charlietap.chasm.declarations.moduleInfo
import io.github.charlietap.chasm.decoder.WasmFunctionBodyOffsets
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.jvm.CompileError.Stage
import io.github.charlietap.chasm.module.ModuleDeclarationsFactory
import io.github.charlietap.chasm.validator.WasmModuleValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream
import java.io.UncheckedIOException
import java.nio.file.Path

suspend fun compile(
    input: Path,
    output: ClassOutput,
    config: CompileConfig,
): ChasmResult<CompiledModuleInfo, CompileError> {
    return runInterruptible(Dispatchers.Default) {
        compile(output, config) { sources -> sources.read(input, config.runtime.stacktraces, config.runtime.limits.memory) }
    }
}

suspend fun compile(
    input: ByteArray,
    output: ClassOutput,
    config: CompileConfig,
): ChasmResult<CompiledModuleInfo, CompileError> {
    return runInterruptible(Dispatchers.Default) {
        compile(output, config) { sources -> sources.read(input, config.runtime.limits.memory) }
    }
}

private inline fun compile(
    output: ClassOutput,
    config: CompileConfig,
    crossinline read: (ModuleSources) -> Result<SourceModule, SourceFailure>,
): ChasmResult<CompiledModuleInfo, CompileError> {
    return compile(
        output = output,
        config = config,
        classNames = ClassNames(),
        sources = ModuleSources(::WasmModuleDecoder, ::WasmModuleDecoder, ::WasmModuleValidator),
        read = read,
        moduleCompiler = { module, types ->
            val runtime = config.runtime
            ModuleCompiler(
                module = module,
                types = types,
                recordProvenance = runtime.stacktraces,
                devirtualiseIndirectCalls = !runtime.stacktraces && runtime.limits.cpu !is CpuLimits.Metered && !runtime.interrupts,
            )
        },
        uncompilableFunctions = ::UncompilableFunctions,
        declarationsFactory = ::ModuleDeclarationsFactory,
        trapSiteSource = TrapSiteSource(::WasmFunctionBodyOffsets, ::TrapSiteTables),
        moduleLowerer = ::ModuleLowerer,
    )
}

internal inline fun compile(
    output: ClassOutput,
    config: CompileConfig,
    classNames: ClassNames,
    sources: ModuleSources,
    crossinline read: (ModuleSources) -> Result<SourceModule, SourceFailure>,
    crossinline moduleCompiler: ModuleCompiler,
    crossinline uncompilableFunctions: UncompilableFunctions,
    crossinline declarationsFactory: ModuleDeclarationsFactory,
    trapSiteSource: TrapSiteSource,
    crossinline moduleLowerer: ModuleLowerer,
): ChasmResult<CompiledModuleInfo, CompileError> {

    if (!classNames.valid(config.className)) {
        return error(Stage.CONFIGURATION, "\"${config.className}\" is not a Java class name")
    }

    val source = read(sources).getOrElse { failure -> return error(stage(failure), failure.message) }
    val options = jvmOptions(config)
    val staged = when (output) {
        is MemoryClassOutput -> output.staging
        is StagedClassOutput -> output
        else -> null
    }

    val result: ChasmResult<CompiledModuleInfo, CompileError> = try {

        val module = source.module
        val types = module.typeResolver()
        val code = moduleCompiler(module, types).getOrElse { failure ->
            val functions = uncompilableFunctions(module, types)
            val reasons = functions.entries.joinToString { (index, reason) -> "$index ($reason)" }
            return error(Stage.GENERATION, "cannot compile functions: ${reasons.ifEmpty { failure.toString() }}", functions.keys.toList())
        }

        val sites = if (config.runtime.stacktraces) {
            trapSiteSource.sites(source, code)
        } else {
            emptyMap()
        }
        val declarations = declarationsFactory(module)
        val adapter = ClassOutputAdapter(output)
        val _ = moduleLowerer(module, code, options, adapter, declarations, sites)

        val rules = shrinkerRules(config)
        if (rules.isNotEmpty()) {
            adapter.resource("META-INF/proguard/${config.className}.pro") { stream -> stream.write(rules.encodeToByteArray()) }
        }
        if (Thread.currentThread().isInterrupted) {
            throw InterruptedException("the compilation was cancelled")
        }
        guardOutput { staged?.commit() }

        val info = moduleInfo(declarations)
        val resourceRoot = config.className.replace('.', '/')
        ChasmResult.Success(
            CompiledModuleInfo(
                config = config,
                imports = info.imports,
                exports = info.exports,
                runtimeAbi = COMPILED_RUNTIME_ABI,
                compilerVersion = BuildConfig.VERSION,
                sourceHash = source.hash,
                diagnostics = if (config.runtime.stacktraces) {
                    Diagnostics(trapSitesResource(resourceRoot), trapLinesResource(resourceRoot))
                } else {
                    null
                },
                classes = adapter.classes.toList(),
                resources = adapter.resources.toList(),
                shrinkerRules = rules,
            ),
        )
    } catch (limit: JarEntryLimitExceeded) {
        error(Stage.OUTPUT, "the module needs more than 65,534 jar entries, which one jar cannot hold: write it with ClassOutput.directory(...)")
    } catch (unsupported: UnsupportedFunctions) {
        error(Stage.GENERATION, unsupported.message.orEmpty(), unsupported.reasons.keys.toList())
    } catch (tooLarge: ClassTooLarge) {
        error(Stage.GENERATION, tooLarge.message.orEmpty())
    } catch (failure: OutputFailure) {
        error(Stage.OUTPUT, "cannot write the compiled module: ${failure.cause?.message}")
    } catch (failure: Throwable) {
        staged?.discard()
        throw failure
    }

    if (result is ChasmResult.Error) {
        staged?.discard()
    }
    return result
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

private fun jvmOptions(config: CompileConfig): JvmOptions {
    return when (config.target) {
        CompileTarget.HOTSPOT -> JvmOptions(
            entryClass = config.className,
            runtime = config.runtime,
            memory = MemoryLowering.BYTE_BUFFER,
            methodSize = BytecodeSize(),
            target = ArtifactTarget.HOTSPOT,
        )
        CompileTarget.ART -> JvmOptions(
            entryClass = config.className,
            runtime = config.runtime,
            memory = MemoryLowering.UNSAFE,
            methodSize = DexUnitEstimate(),
            target = ArtifactTarget.ART,
        )
    }
}

internal class ClassOutputAdapter(
    private val output: ClassOutput,
) : JvmOutput {

    val classes = ArrayList<String>()
    val resources = ArrayList<String>()

    override fun classFile(
        internalName: String,
        bytes: ByteArray,
    ) {

        val className = internalName.replace('/', '.')
        guardOutput { output.classFile(className, bytes) }

        classes += className
    }

    override fun <T> resource(
        path: String,
        write: (OutputStream) -> T,
    ): T {

        val buffer = ByteArrayOutputStream()
        val result = guardOutput { write(buffer) }
        guardOutput { output.resource(path, buffer.toByteArray()) }

        resources += path
        return result
    }
}

internal inline fun <T> guardOutput(
    block: () -> T,
): T = try {
    block()
} catch (failure: OutputFailure) {
    throw failure
} catch (failure: IOException) {
    throw OutputFailure(failure)
} catch (failure: UncheckedIOException) {
    throw OutputFailure(failure)
}

internal class OutputFailure(
    cause: Throwable,
) : RuntimeException(cause)

private fun error(
    stage: Stage,
    message: String,
    functions: List<Int> = emptyList(),
): ChasmResult.Error<CompileError> {
    return ChasmResult.Error(CompileError(message, stage, functions))
}
