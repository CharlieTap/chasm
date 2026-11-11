package io.github.charlietap.chasm.jit.internal

import io.github.charlietap.chasm.compiler.backend.dex.DexOptions
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.util.concurrent.atomic.AtomicInteger

private val unnamed = AtomicInteger()

/**
 * The package a module's compiled code is generated in. For a module with [bytes] it digests them, [options]
 * and this version of chasm, so it also identifies the code in a cache, and takes a suffix when another
 * instance in the store already uses that name ([taken]): compiled stack traces are resolved per store.
 */
internal fun packageName(
    bytes: ByteArray?,
    options: DexOptions,
    taken: Set<String>,
): String {
    if (bytes == null) return UNNAMED_PREFIX + unnamed.incrementAndGet()
    val name = PACKAGE_PREFIX + digest(bytes, options)
    if (name !in taken) return name
    return generateSequence(2) { occurrence -> occurrence + 1 }.map { occurrence -> "${name}_$occurrence" }.first { it !in taken }
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
    digest.update(BuildConfig.CHASM_VERSION.encodeToByteArray())
    digest.update(bytes)
    return digest.digest().joinToString("") { byte -> "%02x".format(byte) }
}

private fun Boolean.byte(): Byte = if (this) 1 else 0

private const val PACKAGE_PREFIX = "chasm.jit.h"

private const val UNNAMED_PREFIX = "chasm.jit.m"

private const val ALGORITHM = "SHA-256"

private const val FORMAT = 1

private const val HEADER_BYTES = 4 + 5 + 8 + 1 + 4 + 4
