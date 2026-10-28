package io.github.charlietap.chasm.compiler.frontend.context

internal class ObjectPool<T : Any> {

    private var items = arrayOfNulls<Any>(INITIAL_CAPACITY)

    var size: Int = 0
        private set

    @Suppress("UNCHECKED_CAST")
    operator fun get(
        index: Int,
    ): T = items[index] as T

    fun add(
        item: T,
    ) {
        if (size == items.size) items = items.copyOf(size * 2)
        items[size++] = item
    }

    private companion object {
        const val INITIAL_CAPACITY = 16
    }
}
