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
 * A key digests the module's bytes, its compilation options and the version of chasm, so an entry is only
 * ever read back for the code it holds. One that cannot be read or loaded is compiled again and replaced.
 * Exceptions thrown by [get] or [put] are ignored, and the module is compiled as though there were no
 * entry. Instances may be created on several threads at once, so implementations must be thread safe.
 *
 * Chasm never removes entries. A changed module or a new version of chasm uses new keys, and the old
 * entries stay until the host evicts them, for instance by size or age. Entries for large modules run to
 * megabytes. Chasm loads what [get] returns as code, so the store behind the cache must be private to the
 * app.
 *
 * Both calls run on the thread creating the instance. [put] may hand the write to another thread, since
 * chasm does not read the value again. Chasm reads the array [get] returns until the instance is created,
 * so it must not change before then.
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
