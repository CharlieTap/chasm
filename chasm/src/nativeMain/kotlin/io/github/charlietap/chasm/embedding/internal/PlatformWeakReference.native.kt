@file:OptIn(kotlin.experimental.ExperimentalNativeApi::class)

package io.github.charlietap.chasm.embedding.internal

import kotlin.native.ref.WeakReference

internal actual class PlatformWeakReference<T : Any> actual constructor(
    referent: T,
) {

    private val reference = WeakReference(referent)

    actual fun get(): T? {
        return reference.value
    }
}
