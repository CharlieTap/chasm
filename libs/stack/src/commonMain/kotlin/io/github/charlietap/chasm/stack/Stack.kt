package io.github.charlietap.chasm.stack

class Stack<T>(
    private val entries: ArrayDeque<T> = ArrayDeque(),
) {
    fun push(value: T) = entries.addLast(value)

    fun popOrNull(): T? = entries.removeLastOrNull()

    fun peekOrNull(): T? = entries.lastOrNull()

    fun peekNthOrNull(n: Int) = entries.getOrNull(entries.lastIndex - n)

    fun depth(): Int = entries.size

    fun entries(): ArrayDeque<T> = entries

    fun clear() = entries.removeAll { true }
}
