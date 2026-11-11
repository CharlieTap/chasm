package io.github.charlietap.chasm.jit.internal

import io.github.charlietap.chasm.compiler.backend.dex.DexModule
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.nio.ByteBuffer

/** A compilation read back from a cache entry; its DEX files are views of the entry, not copies. */
internal class CachedCompilation(
    val dex: List<ByteBuffer>,
    val installer: String,
    val inlineMemory: Boolean,
    val chunkStarts: Map<Int, IntArray>,
)

internal fun encodeCompilation(
    packageName: String,
    module: DexModule,
    version: String = CHASM_VERSION,
): ByteArray {
    val size = Int.SIZE_BYTES * 2 + utfSize(version) + utfSize(packageName) + utfSize(module.installer) + 1 +
        Int.SIZE_BYTES + module.chunkStarts.values.sumOf { starts -> Int.SIZE_BYTES * (2 + starts.size) } +
        Int.SIZE_BYTES + module.dex.sumOf { dex -> Int.SIZE_BYTES + dex.size }
    val bytes = ExactOutput(size)
    DataOutputStream(bytes).use { out ->
        out.writeInt(MAGIC)
        out.writeInt(FORMAT)
        out.writeUTF(version)
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
    return bytes.bytes()
}

/**
 * The compilation [encodeCompilation] wrote for [packageName] with this version of chasm, or null when
 * [bytes] hold anything else, including a truncated or corrupt entry.
 */
internal fun decodeCompilation(
    bytes: ByteArray,
    packageName: String,
    version: String = CHASM_VERSION,
): CachedCompilation? = try {
    val source = ByteArrayInputStream(bytes)
    DataInputStream(source).use { input ->
        if (input.readInt() != MAGIC || input.readInt() != FORMAT) return null
        if (input.readUTF() != version || input.readUTF() != packageName) return null
        val installer = input.readUTF()
        val inlineMemory = input.readBoolean()
        val chunkStarts = HashMap<Int, IntArray>()
        repeat(input.count(bytes.size)) {
            val function = input.readInt()
            chunkStarts[function] = IntArray(input.count(bytes.size)) { input.readInt() }
        }
        val dex = List(input.count(bytes.size)) {
            val size = input.count(source.available())
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

// Package names, installers and versions are ASCII, which modified UTF-8 stores a byte per character.
private fun utfSize(
    value: String,
): Int = Short.SIZE_BYTES + value.length

/** Hands back its buffer without copying when the size it was given turns out to be exact. */
private class ExactOutput(
    size: Int,
) : ByteArrayOutputStream(size) {

    fun bytes(): ByteArray = if (count == buf.size) buf else toByteArray()
}

// A count can never exceed what remains of the entry, so a corrupt one fails here rather than in an allocation.
private fun DataInputStream.count(
    limit: Int,
): Int = readInt().also { count -> if (count !in 0..limit) throw CorruptEntry() }

private class CorruptEntry : RuntimeException()

private const val MAGIC = 0x434A4954

private const val FORMAT = 1
