package io.github.charlietap.chasm.embedding.internal

import java.lang.ref.WeakReference

internal actual class PlatformWeakReference<T : Any> actual constructor(
    referent: T,
) {

    private val reference = WeakReference(referent)

    actual fun get(): T? {
        return reference.get()
    }
}
