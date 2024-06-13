package io.github.charlietap.chasm.weakref

import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalNativeApi::class)
actual fun <T : Any> weakReference(value: T): WeakReference<T> = object : WeakReference<T> {
    val inner = kotlin.native.ref.WeakReference(value)
    override val value: T?
        get() = inner.value
}
