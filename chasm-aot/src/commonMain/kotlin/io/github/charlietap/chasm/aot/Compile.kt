@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.aot

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getError
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.aot.CompileError.Stage
import io.github.charlietap.chasm.aot.internal.COMPILED_RUNTIME_ABI
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.typeResolver
import io.github.charlietap.chasm.compiler.backend.jvm.ClassTooLarge
import io.github.charlietap.chasm.compiler.backend.jvm.JvmOptions
import io.github.charlietap.chasm.compiler.backend.jvm.JvmOutput
import io.github.charlietap.chasm.compiler.backend.jvm.MemoryLowering
import io.github.charlietap.chasm.compiler.backend.jvm.ModuleLowerer
import io.github.charlietap.chasm.compiler.backend.jvm.UnsupportedFunctions
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.chasm.compiler.frontend.ModuleRewriter
import io.github.charlietap.chasm.compiler.frontend.UncompilableFunctions
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.config.StoreLimits
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.module.ModuleDeclarationsFactory
import io.github.charlietap.chasm.stream.SourceReader
import io.github.charlietap.chasm.validator.WasmModuleValidator
import java.io.FilterInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.UncheckedIOException
import java.nio.channels.Channels
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

fun compile(
    input: Path,
    output: CompileOutput,
    config: CompileConfig,
): CompileResult {
    if (config.stacktraces) {
        val bytes = try {
            Files.readAllBytes(input)
        } catch (failure: IOException) {
            return error(Stage.INPUT, "cannot read $input: ${failure.message}")
        }
        return compile(bytes, output, config)
    }
    val channel = try {
        FileChannel.open(input, StandardOpenOption.READ)
    } catch (failure: IOException) {
        return error(Stage.INPUT, "cannot read $input: ${failure.message}")
    }
    return try {
        channel.use {
            compile(output, config, streamSource { Channels.newInputStream(channel.position(0)).buffered() })
        }
    } catch (failure: IOException) {
        error(Stage.INPUT, "cannot read $input: ${failure.message}")
    }
}

fun compile(
    input: ByteArray,
    output: CompileOutput,
    config: CompileConfig,
): CompileResult = compile(output, config, byteArraySource(input), input)

private fun compile(
    output: CompileOutput,
    config: CompileConfig,
    source: ModuleSource,
    binary: ByteArray? = null,
): CompileResult =
    compile(
        output = output,
        config = config,
        source = source,
        binary = binary,
        validator = ::WasmModuleValidator,
        moduleRewriter = { module, types ->
            ModuleRewriter(
                module = module,
                types = types,
                preserveSource = config.stacktraces || config.storeConfig.meterFuel || config.storeConfig.interruptible,
            )
        },
        moduleCompiler = { module, types -> ModuleCompiler(module, types, recordProvenance = config.stacktraces) },
        uncompilableFunctions = ::UncompilableFunctions,
        declarationsFactory = ::ModuleDeclarationsFactory,
        trapSitesFactory = ::TrapSitesFactory,
        moduleLowerer = ::ModuleLowerer,
    )

internal inline fun compile(
    output: CompileOutput,
    config: CompileConfig,
    noinline source: ModuleSource,
    binary: ByteArray?,
    crossinline validator: WasmModuleValidator,
    crossinline moduleRewriter: ModuleRewriter,
    crossinline moduleCompiler: ModuleCompiler,
    crossinline uncompilableFunctions: UncompilableFunctions,
    crossinline declarationsFactory: ModuleDeclarationsFactory,
    crossinline trapSitesFactory: TrapSitesFactory,
    crossinline moduleLowerer: ModuleLowerer,
): CompileResult {
    val options = jvmOptions(config).getOrElse { failure -> return CompileResult.Error(failure) }
    val sink = output.sink()
    val result = try {
        val decoded = decodeForCompilation(config.moduleConfig, source).getOrElse { failure -> return CompileResult.Error(failure) }
        val validated = validator(config.moduleConfig, decoded).getOrElse { failure ->
            return error(Stage.VALIDATION, failure.toString())
        }
        exceededLimit(validated, config.storeConfig.limits)?.let { reason -> return error(Stage.CONFIGURATION, reason) }
        val types = validated.typeResolver()
        val module = moduleRewriter(validated, types)
        val code = moduleCompiler(module, types).getOrElse { failure ->
            val functions = uncompilableFunctions(module, types)
            val reasons = functions.entries.joinToString { (index, reason) -> "$index ($reason)" }
            return error(Stage.GENERATION, "cannot compile functions: ${reasons.ifEmpty { failure.toString() }}", functions.keys.toList())
        }
        val sites = if (config.stacktraces && binary != null) trapSitesFactory(config.moduleConfig, binary, module, code) else emptyMap()
        val generated = moduleLowerer(module, code, options, CompileOutputAdapter(sink), declarationsFactory(module), sites)
        val files = guardOutput { sink.commit() }
        CompileResult.Success(
            CompiledModuleArtifact(
                moduleClassName = generated.moduleClass,
                metadata = metadata(config),
                dataResourcePath = generated.data,
                files = files,
                shrinkerRules = shrinkerRules(generated.moduleClass, config),
            ),
        )
    } catch (unsupported: UnsupportedFunctions) {
        error(Stage.GENERATION, unsupported.message.orEmpty(), unsupported.reasons.keys.toList())
    } catch (tooLarge: ClassTooLarge) {
        error(Stage.GENERATION, tooLarge.message.orEmpty())
    } catch (input: InputFailure) {
        error(Stage.INPUT, "the module's binary changed or could not be read again: ${input.cause?.message}")
    } catch (failure: OutputFailure) {
        error(Stage.OUTPUT, "cannot write the compiled module: ${failure.cause?.message}")
    } catch (failure: Throwable) {
        sink.discard()
        throw failure
    }
    if (result is CompileResult.Error) sink.discard()
    return result
}

private fun exceededLimit(
    module: Module,
    limits: StoreLimits,
): String? {
    val memoryPages = limits.memoryPages?.toULong()
    val tableElements = limits.tableElements?.toULong()
    module.memories.forEachIndexed { index, memory ->
        val pages = memory.type.limits.min
        if (memoryPages != null && pages > memoryPages) {
            return "memory $index starts at $pages pages, above the store's limit of $memoryPages pages"
        }
    }
    module.tables.forEachIndexed { index, table ->
        val elements = table.type.limits.min
        if (tableElements != null && elements > tableElements) {
            return "table $index starts at $elements elements, above the store's limit of $tableElements elements"
        }
    }
    return null
}

@OptIn(ExperimentalChasmApi::class)
private fun metadata(
    config: CompileConfig,
): CompiledModuleMetadata = CompiledModuleMetadata(
    capabilities = Capabilities(
        meterFuel = config.storeConfig.meterFuel,
        interruptible = config.storeConfig.interruptible,
        stacktraces = config.stacktraces,
    ),
    storeConfig = config.storeConfig,
    initialFuel = config.initialFuel,
    runtimeAbi = COMPILED_RUNTIME_ABI,
)

internal typealias ModuleSource = (ModuleConfig) -> Result<Module, CompileError>

internal fun decodeForCompilation(
    config: ModuleConfig,
    source: ModuleSource,
): Result<Module, CompileError> {
    val named = source(config.copy(decodeNameSection = true))
    val failure = named.getError() ?: return named
    return if (failure.stage == Stage.INPUT) named else source(config)
}

private fun byteArraySource(
    binary: ByteArray,
): ModuleSource = { config ->
    WasmModuleDecoder(config, binary).mapError { failure -> CompileError(failure.toString(), Stage.DECODE) }
}

private fun streamSource(
    open: () -> InputStream,
): ModuleSource = { config ->
    val reader = InputStreamSourceReader(ReadGuard(open()))
    WasmModuleDecoder(config, reader).mapError { failure ->
        val input = reader.failure
        if (input != null) {
            CompileError("cannot read the module's binary: ${input.message}", Stage.INPUT)
        } else {
            CompileError(failure.toString(), Stage.DECODE)
        }
    }
}

private fun jvmOptions(
    config: CompileConfig,
): Result<JvmOptions, CompileError> {
    if (!isPackageName(config.packageName)) {
        return Err(CompileError("\"${config.packageName}\" is not a Java package name", Stage.CONFIGURATION))
    }
    if (config.initialFuel != null && !config.storeConfig.meterFuel) {
        return Err(CompileError("initialFuel needs a store that meters fuel", Stage.CONFIGURATION))
    }
    if (config.initialFuel != null && config.initialFuel < 0) {
        return Err(CompileError("initialFuel must not be negative, not ${config.initialFuel}", Stage.CONFIGURATION))
    }
    return try {
        Ok(
            JvmOptions(
                packageName = config.packageName,
                storeConfig = config.storeConfig,
                initialFuel = config.initialFuel,
                memory = when (config.target) {
                    CompileTarget.JVM -> MemoryLowering.HELPERS
                    CompileTarget.ANDROID -> MemoryLowering.UNSAFE
                },
                stacktraces = config.stacktraces,
                maxMethodBytes = config.maxMethodBytes,
                functionMaxMethodBytes = config.functionMaxMethodBytes,
            ),
        )
    } catch (invalid: IllegalArgumentException) {
        Err(CompileError(invalid.message.orEmpty(), Stage.CONFIGURATION))
    }
}

private class ReadGuard(
    input: InputStream,
) : FilterInputStream(input) {

    override fun read(): Int = guard { super.read() }

    override fun read(
        b: ByteArray,
        off: Int,
        len: Int,
    ): Int = guard { super.read(b, off, len) }

    override fun skip(
        n: Long,
    ): Long = guard { super.skip(n) }

    override fun available(): Int = guard { super.available() }

    private inline fun <T> guard(
        block: () -> T,
    ): T = try {
        block()
    } catch (failure: IOException) {
        throw InputFailure(failure)
    }
}

internal class CompileOutputAdapter(
    private val sink: OutputSink,
) : JvmOutput {

    override fun classFile(
        internalName: String,
        bytes: ByteArray,
    ) = guardOutput { sink.classFile(internalName, bytes) }

    override fun <T> resource(
        path: String,
        write: (OutputStream) -> T,
    ): T = guardOutput {
        val results = ArrayList<T>(1)
        sink.resource(path) { stream -> results += write(stream) }
        results.single()
    }
}

internal inline fun <T> guardOutput(
    block: () -> T,
): T = try {
    block()
} catch (failure: InputFailure) {
    throw failure
} catch (failure: OutputFailure) {
    throw failure
} catch (failure: IOException) {
    throw OutputFailure(failure)
} catch (failure: UncheckedIOException) {
    throw OutputFailure(failure)
}

internal class InputFailure(
    cause: Throwable,
) : RuntimeException(cause)

internal class OutputFailure(
    cause: Throwable,
) : RuntimeException(cause)

private class InputStreamSourceReader(
    private val input: InputStream,
) : SourceReader {

    private var next = NOTHING_PEEKED

    var failure: Throwable? = null
        private set

    override fun byte(): Byte {
        val byte = take()
        if (byte < 0) throw NoSuchElementException("the module's binary ends early")
        return byte.toByte()
    }

    override fun bytes(
        amount: Int,
    ): ByteArray {
        val bytes = ByteArray(amount)
        var read = 0
        if (amount > 0 && next >= 0) {
            bytes[read++] = next.toByte()
            next = NOTHING_PEEKED
        }
        while (read < amount) {
            val chunk = read { input.read(bytes, read, amount - read) }
            if (chunk < 0) break
            read += chunk
        }
        return if (read == amount) bytes else bytes.copyOf(read)
    }

    override fun exhausted(): Boolean {
        if (next == NOTHING_PEEKED) next = read { input.read() }
        return next < 0
    }

    override fun peek(): SourceReader = throw UnsupportedOperationException("the binary is read once, in order")

    private fun take(): Int {
        if (next != NOTHING_PEEKED) return next.also { if (it >= 0) next = NOTHING_PEEKED }
        return read { input.read() }
    }

    private inline fun read(
        block: () -> Int,
    ): Int = try {
        block()
    } catch (input: InputFailure) {
        failure = input.cause
        throw input
    }

    private companion object {
        const val NOTHING_PEEKED = -2
    }
}

private fun error(
    stage: Stage,
    message: String,
    functions: List<Int> = emptyList(),
) = CompileResult.Error(CompileError(message, stage, functions))

private fun isPackageName(
    name: String,
): Boolean = name.split('.').all { segment ->
    segment.isNotEmpty() &&
        (segment[0].isJavaIdentifierStart()) &&
        segment.all { character -> character.isJavaIdentifierPart() } &&
        segment !in JAVA_KEYWORDS
}

private val JAVA_KEYWORDS = setOf(
    "abstract",
    "assert",
    "boolean",
    "break",
    "byte",
    "case",
    "catch",
    "char",
    "class",
    "const",
    "continue",
    "default",
    "do",
    "double",
    "else",
    "enum",
    "extends",
    "false",
    "final",
    "finally",
    "float",
    "for",
    "goto",
    "if",
    "implements",
    "import",
    "instanceof",
    "int",
    "interface",
    "long",
    "native",
    "new",
    "null",
    "package",
    "private",
    "protected",
    "public",
    "return",
    "short",
    "static",
    "strictfp",
    "super",
    "switch",
    "synchronized",
    "this",
    "throw",
    "throws",
    "transient",
    "true",
    "try",
    "void",
    "volatile",
    "while",
    "_",
)
