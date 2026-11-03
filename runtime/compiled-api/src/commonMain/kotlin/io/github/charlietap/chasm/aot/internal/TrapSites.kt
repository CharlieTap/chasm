package io.github.charlietap.chasm.aot.internal

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.diagnostic.FunctionTrapSites
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

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

    fun string(): String {
        val length = count()
        return bytes.decodeToString(position, position + length).also { position += length }
    }

    fun finished(): Boolean = position == bytes.size
}

private const val MAGIC = 0x43545331
