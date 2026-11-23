@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.aot

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getError
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.aot.CompileError.Stage
import io.github.charlietap.chasm.aot.internal.COMPILED_RUNTIME_ABI
import io.github.charlietap.chasm.aot.internal.trapLinesResource
import io.github.charlietap.chasm.aot.internal.trapSitesResource
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.typeResolver
import io.github.charlietap.chasm.compiler.backend.jvm.ClassTooLarge
import io.github.charlietap.chasm.compiler.backend.jvm.JvmOptions
import io.github.charlietap.chasm.compiler.backend.jvm.JvmOutput
import io.github.charlietap.chasm.compiler.backend.jvm.MemoryLowering
import io.github.charlietap.chasm.compiler.backend.jvm.ModuleLowerer
import io.github.charlietap.chasm.compiler.backend.jvm.UnsupportedFunctions
import io.github.charlietap.chasm.compiler.backend.jvm.size.BytecodeSize
import io.github.charlietap.chasm.compiler.backend.jvm.size.DexUnitEstimate
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.chasm.compiler.frontend.UncompilableFunctions
import io.github.charlietap.chasm.config.Bytes
import io.github.charlietap.chasm.config.CpuLimits
import io.github.charlietap.chasm.config.MemoryLimits
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.declarations.moduleInfo
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.module.ModuleDeclarationsFactory
import io.github.charlietap.chasm.stream.SourceReader
import io.github.charlietap.chasm.validator.WasmModuleValidator
import java.io.ByteArrayOutputStream
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
import java.security.DigestInputStream
import java.security.MessageDigest

fun compile(
    input: Path,
    output: ClassOutput,
    config: CompileConfig,
): ChasmResult<CompiledModuleInfo, CompileError> {

    if (config.runtime.stacktraces) {
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

            val digest = MessageDigest.getInstance("SHA-256")
            DigestInputStream(Channels.newInputStream(channel), digest).transferTo(OutputStream.nullOutputStream())

            val source = streamSource { Channels.newInputStream(channel.position(0)).buffered() }
            compile(output, config, source, null, digest.digest().toHexString())
        }
    } catch (failure: IOException) {
        error(Stage.INPUT, "cannot read $input: ${failure.message}")
    }
}

fun compile(
    input: ByteArray,
    output: ClassOutput,
    config: CompileConfig,
): ChasmResult<CompiledModuleInfo, CompileError> {
    val sourceHash = MessageDigest.getInstance("SHA-256").digest(input).toHexString()
    return compile(output, config, byteArraySource(input), input, sourceHash)
}

private fun compile(
    output: ClassOutput,
    config: CompileConfig,
    source: ModuleSource,
    binary: ByteArray?,
    sourceHash: String,
): ChasmResult<CompiledModuleInfo, CompileError> {
    return compile(
        output = output,
        config = config,
        source = source,
        binary = binary,
        sourceHash = sourceHash,
        validator = ::WasmModuleValidator,
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
        trapSitesFactory = ::TrapSitesFactory,
        moduleLowerer = ::ModuleLowerer,
    )
}

internal inline fun compile(
    output: ClassOutput,
    config: CompileConfig,
    noinline source: ModuleSource,
    binary: ByteArray?,
    sourceHash: String,
    crossinline validator: WasmModuleValidator,
    crossinline moduleCompiler: ModuleCompiler,
    crossinline uncompilableFunctions: UncompilableFunctions,
    crossinline declarationsFactory: ModuleDeclarationsFactory,
    crossinline trapSitesFactory: TrapSitesFactory,
    crossinline moduleLowerer: ModuleLowerer,
): ChasmResult<CompiledModuleInfo, CompileError> {

    val options = jvmOptions(config).getOrElse { failure -> return ChasmResult.Error(failure) }
    val staged = when (output) {
        is MemoryClassOutput -> output.staging
        is StagedClassOutput -> output
        else -> null
    }

    val result: ChasmResult<CompiledModuleInfo, CompileError> = try {

        val moduleConfig = ModuleConfig()
        val decoded = decodeForCompilation(moduleConfig, source).getOrElse { failure -> return ChasmResult.Error(failure) }
        val module = validator(moduleConfig, decoded).getOrElse { failure ->
            return error(Stage.VALIDATION, failure.toString())
        }
        exceededLimit(module, config.runtime.limits.memory)?.let { reason -> return error(Stage.CONFIGURATION, reason) }

        val types = module.typeResolver()
        val code = moduleCompiler(module, types).getOrElse { failure ->
            val functions = uncompilableFunctions(module, types)
            val reasons = functions.entries.joinToString { (index, reason) -> "$index ($reason)" }
            return error(Stage.GENERATION, "cannot compile functions: ${reasons.ifEmpty { failure.toString() }}", functions.keys.toList())
        }

        val sites = if (config.runtime.stacktraces && binary != null) trapSitesFactory(moduleConfig, binary, module, code) else emptyMap()
        val declarations = declarationsFactory(module)
        val adapter = ClassOutputAdapter(output)
        val _ = moduleLowerer(module, code, options, adapter, declarations, sites)

        val rules = shrinkerRules(config)
        if (rules.isNotEmpty()) {
            adapter.resource("META-INF/proguard/${config.className}.pro") { stream -> stream.write(rules.encodeToByteArray()) }
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
                sourceHash = sourceHash,
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
    } catch (input: InputFailure) {
        error(Stage.INPUT, "the module's binary changed or could not be read again: ${input.cause?.message}")
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

private fun exceededLimit(
    module: Module,
    limits: MemoryLimits,
): String? {

    val linearMemory = limits.linearMemory
    val tables = limits.tables

    module.memories.forEachIndexed { index, memory ->
        val pages = memory.type.limits.min
        if (linearMemory != null && pages > (linearMemory.value / Bytes.PAGE).toULong()) {
            return "memory $index starts at $pages pages, above the limit of $linearMemory"
        }
    }

    module.tables.forEachIndexed { index, table ->
        val elements = table.type.limits.min
        if (tables != null && elements > (tables.value / TABLE_ELEMENT_BYTES).toULong()) {
            return "table $index starts at $elements elements, above the limit of $tables"
        }
    }

    return null
}

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

    if (!isClassName(config.className)) {
        return Err(CompileError("\"${config.className}\" is not a Java class name", Stage.CONFIGURATION))
    }

    return Ok(
        JvmOptions(
            entryClass = config.className,
            runtime = config.runtime,
            memory = when (config.target) {
                CompileTarget.JVM -> MemoryLowering.BYTE_BUFFER
                CompileTarget.ANDROID -> MemoryLowering.UNSAFE
            },
            methodSize = when (config.target) {
                CompileTarget.JVM -> BytecodeSize()
                CompileTarget.ANDROID -> DexUnitEstimate()
            },
        ),
    )
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
): ChasmResult.Error<CompileError> {
    return ChasmResult.Error(CompileError(message, stage, functions))
}

private fun isClassName(
    name: String,
): Boolean = name.split('.').all { segment ->
    segment.isNotEmpty() &&
        (segment[0].isJavaIdentifierStart()) &&
        segment.all { character -> character.isJavaIdentifierPart() && character != '$' } &&
        segment !in JAVA_KEYWORDS
}

private const val TABLE_ELEMENT_BYTES = 8L

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
