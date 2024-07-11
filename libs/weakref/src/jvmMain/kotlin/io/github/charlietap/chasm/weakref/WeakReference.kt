package io.github.charlietap.chasm.weakref

actual fun <T : Any> weakReference(value: T): WeakReference<T> = object : WeakReference<T> {
    val inner = java.lang.ref.WeakReference(value)
    override val value: T?
        get() = inner.get()
}
