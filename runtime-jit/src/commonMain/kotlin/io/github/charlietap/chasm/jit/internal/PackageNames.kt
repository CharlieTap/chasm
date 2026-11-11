package io.github.charlietap.chasm.jit.internal

import io.github.charlietap.chasm.compiler.backend.dex.DexOptions
import java.nio.ByteBuffer
import java.security.MessageDigest

internal class PackageNames {

    private val loaded = HashMap<String, Int>()
    private var unnamed = 0

    @Synchronized
    fun next(
        bytes: ByteArray?,
        options: DexOptions,
    ): String {
        if (bytes == null) return UNNAMED_PREFIX + ++unnamed
        val name = PACKAGE_PREFIX + digest(bytes, options)
        val previous = loaded[name]
        if (previous == null && loaded.size >= MAX_NAMES) return UNNAMED_PREFIX + ++unnamed
        val occurrence = (previous ?: 0) + 1
        loaded[name] = occurrence
        return if (occurrence == 1) name else "${name}_$occurrence"
    }

    private fun digest(
        bytes: ByteArray,
        options: DexOptions,
    ): String {
        val header = ByteBuffer.allocate(HEADER_BYTES)
            .putInt(FORMAT)
            .put(options.stacktraces.byte())
            .put(options.meterFuel.byte())
            .put(options.interruptible.byte())
            .put(options.inlineMemory.byte())
            .put((options.byteArrayBaseOffset != null).byte())
            .putLong(options.byteArrayBaseOffset ?: 0L)
            .put((options.splitInstructions != null).byte())
            .putInt(options.splitInstructions ?: 0)
            .putInt(bytes.size)
        val digest = MessageDigest.getInstance(ALGORITHM)
        digest.update(header.array())
        digest.update(bytes)
        return digest.digest().joinToString("") { byte -> "%02x".format(byte) }
    }

    private fun Boolean.byte(): Byte = if (this) 1 else 0

    companion object {

        /** Whether [name] was derived from a module's bytes, so it names the same code in every process. */
        fun hashed(
            name: String,
        ): Boolean = name.startsWith(PACKAGE_PREFIX)
    }
}

private const val PACKAGE_PREFIX = "chasm.jit.h"

private const val UNNAMED_PREFIX = "chasm.jit.m"

private const val ALGORITHM = "SHA-256"

private const val FORMAT = 1

private const val MAX_NAMES = 1_024

private const val HEADER_BYTES = 4 + 5 + 8 + 1 + 4 + 4
