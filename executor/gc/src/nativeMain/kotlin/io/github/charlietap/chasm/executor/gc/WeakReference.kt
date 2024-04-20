package io.github.charlietap.chasm.executor.gc

import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalNativeApi::class)
actual fun <T : Any> weakReference(value: T): WeakReference<T> = object : WeakReference<T> {
    val inner = kotlin.native.ref.WeakReference(value)
    override val value: T?
        get() = inner.value
}
