package io.github.charlietap.chasm.jit.internal

import io.github.charlietap.chasm.InternalChasmApi

@InternalChasmApi
interface CompilationCache {

    fun get(
        key: String,
    ): ByteArray?

    fun put(
        key: String,
        value: ByteArray,
    )
}
