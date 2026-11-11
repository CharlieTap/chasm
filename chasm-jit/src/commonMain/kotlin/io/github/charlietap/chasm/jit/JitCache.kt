@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.jit.internal.CompilationCache

/**
 * Keeps the DEX chasm compiles for a module, so a later run can instantiate the same module without
 * compiling it again. Chasm chooses the keys and the format of the values; keys contain only letters,
 * digits, `.` and `_`, so they can be used as file names.
 *
 * An entry written by another version of chasm, or one that cannot be read or loaded, is compiled again
 * and replaced. Exceptions thrown by [get] or [put] are ignored, and the module is compiled as though
 * there were no entry. Instances may be created on several threads at once, so implementations must be
 * thread safe.
 */
@ExperimentalChasmApi
interface JitCache {

    fun get(
        key: String,
    ): ByteArray?

    fun put(
        key: String,
        value: ByteArray,
    )
}

internal fun JitCache.asCompilationCache(): CompilationCache = object : CompilationCache {

    override fun get(
        key: String,
    ): ByteArray? = this@asCompilationCache.get(key)

    override fun put(
        key: String,
        value: ByteArray,
    ) = this@asCompilationCache.put(key, value)
}
