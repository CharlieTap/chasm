package io.github.charlietap.chasm.compiler.driver

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getError
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.config.Bytes
import io.github.charlietap.chasm.config.MemoryLimits
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.error.ModuleDecoderError
import io.github.charlietap.chasm.stream.SourceReader
import io.github.charlietap.chasm.type.AddressType
import io.github.charlietap.chasm.validator.WasmModuleValidator
import java.io.FilterInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.channels.Channels
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.security.MessageDigest

typealias BinaryModuleDecoder = (ModuleConfig, ByteArray) -> Result<Module, ModuleDecoderError>

typealias StreamedModuleDecoder = (ModuleConfig, SourceReader) -> Result<Module, ModuleDecoderError>

class ModuleSources(
    private val binaryDecoder: BinaryModuleDecoder,
    private val streamedDecoder: StreamedModuleDecoder,
    private val validator: WasmModuleValidator,
) {

    fun read(
        input: ByteArray,
        limits: MemoryLimits,
    ): Result<SourceModule, SourceFailure> {
        val hash = MessageDigest.getInstance(HASH_ALGORITHM).digest(input).toHexString()
        return read(binarySource(input), input, hash, limits)
    }

    fun read(
        input: Path,
        keepBinary: Boolean,
        limits: MemoryLimits,
    ): Result<SourceModule, SourceFailure> {

        if (keepBinary) {
            val bytes = try {
                Files.readAllBytes(input)
            } catch (failure: IOException) {
                return Err(unreadable(input, failure))
            }
            return read(bytes, limits)
        }

        val channel = try {
            FileChannel.open(input, StandardOpenOption.READ)
        } catch (failure: IOException) {
            return Err(unreadable(input, failure))
        }

        return try {
            channel.use {
                val hash = streamedHash(Channels.newInputStream(channel))
                val source = streamSource { Channels.newInputStream(channel.position(0)).buffered() }
                read(source, null, hash, limits)
            }
        } catch (failure: IOException) {
            Err(unreadable(input, failure))
        }
    }

    internal fun streamSource(open: () -> InputStream): ModuleSource {
        return { config ->
            val reader = InputStreamSourceReader(ReadGuard(open()))
            streamedDecoder(config, reader).mapError { failure ->
                val input = reader.failure
                if (input != null) {
                    SourceFailure.Unreadable("cannot read the module's binary: ${input.message}")
                } else {
                    SourceFailure.Undecodable(failure.toString())
                }
            }
        }
    }

    private fun binarySource(binary: ByteArray): ModuleSource {
        return { config ->
            binaryDecoder(config, binary).mapError { failure -> SourceFailure.Undecodable(failure.toString()) }
        }
    }

    private fun read(
        source: ModuleSource,
        binary: ByteArray?,
        hash: String,
        limits: MemoryLimits,
    ): Result<SourceModule, SourceFailure> {

        val config = ModuleConfig()
        val decoded = decodeForCompilation(config, source).getOrElse { failure -> return Err(failure) }
        val module = validator(config, decoded).getOrElse { failure -> return Err(SourceFailure.Invalid(failure.toString())) }
        exceededLimit(module, limits)?.let { reason -> return Err(SourceFailure.OverLimit(reason)) }

        if (usesMemory64(module)) {
            return Err(SourceFailure.Unsupported("memory64 is unsupported"))
        }

        return Ok(SourceModule(module, binary, hash))
    }

    private fun streamedHash(input: InputStream): String {

        val digest = MessageDigest.getInstance(HASH_ALGORITHM)
        val buffer = ByteArray(HASH_BUFFER_BYTES)

        var count = input.read(buffer)
        while (count != -1) {
            digest.update(buffer, 0, count)
            count = input.read(buffer)
        }

        return digest.digest().toHexString()
    }

    private fun unreadable(
        input: Path,
        failure: IOException,
    ): SourceFailure {
        return SourceFailure.Unreadable("cannot read $input: ${failure.message}")
    }

    private companion object {
        const val HASH_ALGORITHM = "SHA-256"
        const val HASH_BUFFER_BYTES = 64 * 1024
    }
}

internal typealias ModuleSource = (ModuleConfig) -> Result<Module, SourceFailure>

internal fun decodeForCompilation(
    config: ModuleConfig,
    source: ModuleSource,
): Result<Module, SourceFailure> {
    val named = source(config.copy(decodeNameSection = true))
    val failure = named.getError() ?: return named
    return if (failure is SourceFailure.Unreadable) named else source(config)
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

private fun usesMemory64(module: Module): Boolean {

    val memoryAddressTypes = module.imports.mapNotNull { import -> (import.descriptor as? Import.Descriptor.Memory)?.type?.addressType } +
        module.memories.map { memory -> memory.type.addressType }
    val tableAddressTypes = module.imports.mapNotNull { import -> (import.descriptor as? Import.Descriptor.Table)?.type?.addressType } +
        module.tables.map { table -> table.type.addressType }

    return (memoryAddressTypes + tableAddressTypes).any { addressType -> addressType == AddressType.I64 }
}

private class ReadGuard(
    input: InputStream,
) : FilterInputStream(input) {

    override fun read(): Int {
        return guard { super.read() }
    }

    override fun read(
        b: ByteArray,
        off: Int,
        len: Int,
    ): Int {
        return guard { super.read(b, off, len) }
    }

    override fun skip(n: Long): Long {
        return guard { super.skip(n) }
    }

    override fun available(): Int {
        return guard { super.available() }
    }

    private inline fun <T> guard(block: () -> T): T {
        return try {
            block()
        } catch (failure: IOException) {
            throw InputFailure(failure)
        }
    }
}

private class InputFailure(
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
        if (byte < 0) {
            throw NoSuchElementException("the module's binary ends early")
        }

        return byte.toByte()
    }

    override fun bytes(amount: Int): ByteArray {

        val bytes = ByteArray(amount)
        var read = 0
        if (amount > 0 && next >= 0) {
            bytes[read++] = next.toByte()
            next = NOTHING_PEEKED
        }

        while (read < amount) {
            val chunk = read { input.read(bytes, read, amount - read) }
            if (chunk < 0) {
                break
            }
            read += chunk
        }

        return if (read == amount) bytes else bytes.copyOf(read)
    }

    override fun exhausted(): Boolean {

        if (next == NOTHING_PEEKED) {
            next = read { input.read() }
        }

        return next < 0
    }

    override fun peek(): SourceReader {
        throw UnsupportedOperationException("the binary is read once, in order")
    }

    private fun take(): Int {

        if (next == NOTHING_PEEKED) {
            return read { input.read() }
        }

        val peeked = next
        if (peeked >= 0) {
            next = NOTHING_PEEKED
        }

        return peeked
    }

    private inline fun read(block: () -> Int): Int {
        return try {
            block()
        } catch (input: InputFailure) {
            failure = input.cause
            throw input
        }
    }

    private companion object {
        const val NOTHING_PEEKED = -2
    }
}

private const val TABLE_ELEMENT_BYTES = 8L
