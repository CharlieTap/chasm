package io.github.charlietap.chasm.executor.gc

interface WeakReference<T> {
    val value: T?
}

expect fun <T : Any> weakReference(value: T): WeakReference<T>
