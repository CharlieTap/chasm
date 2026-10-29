package io.github.charlietap.chasm.aot

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getError
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.aot.CompileError.Stage
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.typeResolver
import io.github.charlietap.chasm.compiler.backend.jvm.ClassTooLarge
import io.github.charlietap.chasm.compiler.backend.jvm.JvmOptions
import io.github.charlietap.chasm.compiler.backend.jvm.JvmOutput
import io.github.charlietap.chasm.compiler.backend.jvm.MemoryLowering
import io.github.charlietap.chasm.compiler.backend.jvm.ModuleLowerer
import io.github.charlietap.chasm.compiler.backend.jvm.UnsupportedFunctions
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.chasm.compiler.frontend.UncompilableFunctions
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.module.ModuleDeclarationsFactory
import io.github.charlietap.chasm.stream.SourceReader
import io.github.charlietap.chasm.validator.WasmModuleValidator
import java.io.FilterInputStream
import java.io.FilterOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.UncheckedIOException
import java.nio.channels.Channels
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption

fun compile(
    binary: Path,
    output: CompileOutput,
    config: CompileConfig,
): CompileResult {
    val channel = try {
        FileChannel.open(binary, StandardOpenOption.READ)
    } catch (failure: IOException) {
        return error(Stage.INPUT, "cannot read $binary: ${failure.message}")
    }
    return try {
        channel.use {
            compile(output, config, streamSource { Channels.newInputStream(channel.position(0)).buffered() })
        }
    } catch (failure: IOException) {
        error(Stage.INPUT, "cannot read $binary: ${failure.message}")
    }
}

fun compile(
    binary: ByteArray,
    output: CompileOutput,
    config: CompileConfig,
): CompileResult = compile(output, config, byteArraySource(binary))

private fun compile(
    output: CompileOutput,
    config: CompileConfig,
    source: ModuleSource,
): CompileResult =
    compile(
        output = output,
        config = config,
        source = source,
        validator = ::WasmModuleValidator,
        moduleCompiler = ::ModuleCompiler,
        uncompilableFunctions = ::UncompilableFunctions,
        declarationsFactory = ::ModuleDeclarationsFactory,
        moduleLowerer = ::ModuleLowerer,
    )

internal inline fun compile(
    output: CompileOutput,
    config: CompileConfig,
    noinline source: ModuleSource,
    crossinline validator: WasmModuleValidator,
    crossinline moduleCompiler: ModuleCompiler,
    crossinline uncompilableFunctions: UncompilableFunctions,
    crossinline declarationsFactory: ModuleDeclarationsFactory,
    crossinline moduleLowerer: ModuleLowerer,
): CompileResult {
    val options = jvmOptions(config).getOrElse { failure -> return CompileResult.Error(failure) }
    return try {
        val decoded = decodeForCompilation(config.moduleConfig, source).getOrElse { failure -> return CompileResult.Error(failure) }
        val module = validator(config.moduleConfig, decoded).getOrElse { failure ->
            return error(Stage.VALIDATION, failure.toString())
        }
        val types = module.typeResolver()
        val code = moduleCompiler(module, types).getOrElse { failure ->
            val functions = uncompilableFunctions(module, types)
            val reasons = functions.entries.joinToString { (index, reason) -> "$index ($reason)" }
            return error(Stage.GENERATION, "cannot compile functions: ${reasons.ifEmpty { failure.toString() }}", functions.keys.toList())
        }
        val generated = moduleLowerer(module, code, options, CompileOutputAdapter(output), declarationsFactory(module))
        CompileResult.Success(CompiledModuleArtifact(generated.moduleClass, generated.data))
    } catch (unsupported: UnsupportedFunctions) {
        error(Stage.GENERATION, unsupported.message.orEmpty(), unsupported.reasons.keys.toList())
    } catch (tooLarge: ClassTooLarge) {
        error(Stage.GENERATION, tooLarge.message.orEmpty())
    } catch (input: InputFailure) {
        error(Stage.INPUT, "the module's binary changed or could not be read again: ${input.cause?.message}")
    } catch (failure: OutputFailure) {
        error(Stage.OUTPUT, "cannot write the compiled module: ${failure.cause?.message}")
    }
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
    if (!isPackageName(config.packageName)) {
        return Err(CompileError("\"${config.packageName}\" is not a Java package name", Stage.CONFIGURATION))
    }
    return try {
        Ok(
            JvmOptions(
                packageName = config.packageName,
                meterFuel = config.meterFuel,
                interruptible = config.interruptible,
                memory = when (config.target) {
                    CompileTarget.JVM -> MemoryLowering.HELPERS
                    CompileTarget.ANDROID -> MemoryLowering.UNSAFE
                },
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
    private val output: CompileOutput,
) : JvmOutput {

    override fun classFile(
        internalName: String,
        bytes: ByteArray,
    ) = guard { output.classFile(internalName, bytes) }

    override fun <T> resource(
        path: String,
        write: (OutputStream) -> T,
    ): T = guard {
        var written = false
        val results = ArrayList<T>(1)
        output.resource(path) { stream ->
            check(!written) { "the resource $path was written twice" }
            written = true
            results += write(stream)
        }
        check(results.isNotEmpty()) { "the output did not write the resource $path" }
        results.single()
    }

    private inline fun <T> guard(
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
    } catch (failure: IllegalStateException) {
        throw OutputFailure(failure)
    }
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
