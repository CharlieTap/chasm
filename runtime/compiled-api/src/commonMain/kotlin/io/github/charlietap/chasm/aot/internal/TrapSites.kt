package io.github.charlietap.chasm.aot.internal

import io.github.charlietap.chasm.InternalChasmApi
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

/**
 * Where each compiled instruction of a function came from in the Wasm
 * binary. [chunkStarts] holds the first compiled instruction of each chunk
 * method when the function was split, and is empty otherwise.
 */
@InternalChasmApi
class FunctionTrapSites(
    val chunkStarts: IntArray,
    private val offsets: IntArray,
    private val memories: IntArray = IntArray(0),
) {
    val size: Int get() = offsets.size

    /** The Wasm offset of the compiled instruction at [position], or -1. */
    fun offset(
        position: Int,
    ): Int = if (position in offsets.indices) offsets[position] else -1

    /**
     * The memory index the memory instruction at [position] accesses, or -1.
     * A copy names its source when [source] is true and its destination
     * otherwise.
     */
    fun memoryIndex(
        position: Int,
        source: Boolean,
    ): Int {
        val packed = packedMemories(position)
        val first = (packed and 0xFFFF) - 1
        val second = (packed ushr 16) - 1
        return if (second >= 0 && !source) second else first
    }

    fun withChunkStarts(
        chunkStarts: IntArray,
    ): FunctionTrapSites = FunctionTrapSites(chunkStarts, offsets, memories)

    internal fun packedMemories(
        position: Int,
    ): Int = if (position in memories.indices) memories[position] else 0
}

@InternalChasmApi
class TrapSites(
    private val functions: Map<Int, FunctionTrapSites>,
) {
    fun function(
        index: Int,
    ): FunctionTrapSites? = functions[index]
}

@InternalChasmApi
fun trapSitesResource(
    packagePath: String,
): String = "$packagePath/sites.$TRAP_SITES_EXTENSION"

@InternalChasmApi
const val TRAP_SITES_EXTENSION = "chasmsites"

// Generated function methods are named FUNCTION_METHOD_PREFIX + index, and the chunks of a split function
// add CHUNK_METHOD_SEPARATOR + chunk; they live in classes named with CODE_CLASS_PREFIX or CHUNK_CLASS_PREFIX.
@InternalChasmApi
const val FUNCTION_METHOD_PREFIX = "f"

@InternalChasmApi
const val CHUNK_METHOD_SEPARATOR = "\$c"

@InternalChasmApi
const val CODE_CLASS_PREFIX = "Code"

@InternalChasmApi
const val CHUNK_CLASS_PREFIX = "Chunks"

// The interpreter's entry into compiled code as the shrinker rules name it, before any renaming.
@InternalChasmApi
const val COMPILED_ENTRY_CLASS = "io.github.charlietap.chasm.executor.invoker.compiled.CompiledEntry"

// The source file classes compiled with trap diagnostics name, which tells them apart from classes that share their names.
@InternalChasmApi
const val TRAP_DIAGNOSTICS_SOURCE_FILE = "module.wasm"

@InternalChasmApi
fun writeTrapSites(
    output: OutputStream,
    functions: Map<Int, FunctionTrapSites>,
) {
    val buffer = ByteArrayOutputStream()
    buffer.writeInt(MAGIC)
    buffer.writeUnsigned(functions.size)
    functions.toSortedMap().forEach { (index, sites) ->
        buffer.writeUnsigned(index)
        buffer.writeUnsigned(sites.chunkStarts.size)
        sites.chunkStarts.forEach { start -> buffer.writeUnsigned(start) }
        buffer.writeUnsigned(sites.size)
        var previous = 0
        for (position in 0 until sites.size) {
            val value = sites.offset(position) + 1
            buffer.writeSigned(value - previous)
            previous = value
        }
        val memories = (0 until sites.size).filter { position -> sites.packedMemories(position) != 0 }
        buffer.writeUnsigned(memories.size)
        var last = 0
        memories.forEach { position ->
            buffer.writeUnsigned(position - last)
            buffer.writeUnsigned(sites.packedMemories(position))
            last = position
        }
    }
    buffer.writeTo(output)
}

/** Null when [input] is not a trap site table this runtime can read. */
@InternalChasmApi
fun readTrapSites(
    input: InputStream,
): TrapSites? = try {
    val reader = Reader(input.readBytes())
    if (reader.int() != MAGIC) {
        null
    } else {
        val functions = HashMap<Int, FunctionTrapSites>()
        repeat(reader.count()) {
            val index = reader.unsigned()
            val chunkStarts = IntArray(reader.count()) { reader.unsigned() }
            var previous = 0
            val offsets = IntArray(reader.count()) {
                previous += reader.signed()
                previous - 1
            }
            val memoryCount = reader.count()
            val memories = if (memoryCount == 0) IntArray(0) else IntArray(offsets.size)
            var position = 0
            repeat(memoryCount) {
                position += reader.unsigned()
                memories[position] = reader.unsigned()
            }
            functions[index] = FunctionTrapSites(chunkStarts, offsets, memories)
        }
        if (reader.finished()) TrapSites(functions) else null
    }
} catch (_: IndexOutOfBoundsException) {
    null
} catch (_: IllegalArgumentException) {
    null
}

internal fun ByteArrayOutputStream.writeInt(
    value: Int,
) {
    write(value ushr 24)
    write(value ushr 16)
    write(value ushr 8)
    write(value)
}

internal fun ByteArrayOutputStream.writeUnsigned(
    value: Int,
) {
    require(value >= 0) { "negative unsigned value $value" }
    var remaining = value
    while (remaining >= 0x80) {
        write(remaining and 0x7F or 0x80)
        remaining = remaining ushr 7
    }
    write(remaining)
}

private fun ByteArrayOutputStream.writeSigned(
    value: Int,
) = writeUnsigned((value shl 1) xor (value shr 31))

internal class Reader(
    private val bytes: ByteArray,
) {
    private var position = 0

    fun int(): Int {
        var value = 0
        repeat(4) { value = (value shl 8) or (bytes[position++].toInt() and 0xFF) }
        return value
    }

    fun unsigned(): Int {
        var result = 0
        var shift = 0
        while (true) {
            val byte = bytes[position++].toInt() and 0xFF
            require(shift < 35) { "varint too long" }
            result = result or ((byte and 0x7F) shl shift)
            if (byte and 0x80 == 0) return result
            shift += 7
        }
    }

    // Every counted value takes at least a byte, so a larger count is corrupt.
    fun count(): Int = unsigned().also { count ->
        require(count in 0..bytes.size - position) { "count $count exceeds the remaining bytes" }
    }

    fun signed(): Int {
        val encoded = unsigned()
        return (encoded ushr 1) xor -(encoded and 1)
    }

    fun finished(): Boolean = position == bytes.size
}

private const val MAGIC = 0x43545331
