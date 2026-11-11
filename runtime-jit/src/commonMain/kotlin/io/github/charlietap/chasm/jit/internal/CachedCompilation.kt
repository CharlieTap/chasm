package io.github.charlietap.chasm.jit.internal

import io.github.charlietap.chasm.compiler.backend.dex.DexModule
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.nio.ByteBuffer

internal class CachedCompilation(
    val dex: List<ByteBuffer>,
    val installer: String,
    val inlineMemory: Boolean,
    val chunkStarts: Map<Int, IntArray>,
)

internal fun DexModule.toCachedCompilation(): CachedCompilation =
    CachedCompilation(dex.map(ByteBuffer::wrap), installer, inlineMemory, chunkStarts)

internal fun encodeCompilation(
    packageName: String,
    module: DexModule,
): ByteArray {
    val bytes = ByteArrayOutputStream()
    DataOutputStream(bytes).use { out ->
        out.writeInt(MAGIC)
        out.writeInt(FORMAT)
        out.writeUTF(packageName)
        out.writeUTF(module.installer)
        out.writeBoolean(module.inlineMemory)
        out.writeInt(module.chunkStarts.size)
        module.chunkStarts.forEach { (function, starts) ->
            out.writeInt(function)
            out.writeInt(starts.size)
            starts.forEach(out::writeInt)
        }
        out.writeInt(module.dex.size)
        module.dex.forEach { dex ->
            out.writeInt(dex.size)
            out.write(dex)
        }
    }
    return bytes.toByteArray()
}

/**
 * The compilation [encodeCompilation] wrote for [packageName], or null when [bytes] hold anything else,
 * including a truncated or corrupt entry. Its DEX files are views of [bytes], not copies.
 */
internal fun decodeCompilation(
    bytes: ByteArray,
    packageName: String,
): CachedCompilation? = try {
    val source = ByteArrayInputStream(bytes)
    DataInputStream(source).use { input ->
        if (input.readInt() != MAGIC || input.readInt() != FORMAT || input.readUTF() != packageName) return null
        val installer = input.readUTF()
        val inlineMemory = input.readBoolean()
        val chunkStarts = HashMap<Int, IntArray>()
        repeat(input.count { source.available() / CHUNK_HEADER }) {
            val function = input.readInt()
            chunkStarts[function] = IntArray(input.count { source.available() / Int.SIZE_BYTES }) { input.readInt() }
        }
        val dex = List(input.count { source.available() / Int.SIZE_BYTES }) {
            val size = input.count { source.available() }
            val offset = bytes.size - source.available()
            input.skipBytes(size)
            // Not sliced: ART reads a heap buffer from its position in array() and ignores arrayOffset.
            ByteBuffer.wrap(bytes, offset, size)
        }
        if (source.available() != 0) return null
        CachedCompilation(dex, installer, inlineMemory, chunkStarts)
    }
} catch (_: IOException) {
    null
} catch (_: CorruptEntry) {
    null
}

// A count can never exceed what the rest of the entry, measured after the count itself, could hold, so a
// corrupt one fails here rather than in an allocation.
private inline fun DataInputStream.count(
    limit: () -> Int,
): Int = readInt().also { count -> if (count !in 0..limit()) throw CorruptEntry() }

private class CorruptEntry : RuntimeException()

private const val MAGIC = 0x434A4954

private const val FORMAT = 1

private const val CHUNK_HEADER = Int.SIZE_BYTES * 2
