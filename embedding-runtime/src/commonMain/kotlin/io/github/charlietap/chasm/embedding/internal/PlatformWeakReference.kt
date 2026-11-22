package io.github.charlietap.chasm.embedding.internal

internal expect class PlatformWeakReference<T : Any>(
    referent: T,
) {

    fun get(): T?
}
