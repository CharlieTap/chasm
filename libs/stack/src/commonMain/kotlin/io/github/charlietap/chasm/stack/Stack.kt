package io.github.charlietap.chasm.stack

import androidx.collection.CircularArray
import kotlin.jvm.JvmInline

@JvmInline
value class Stack<T>(
    private val entries: CircularArray<T>,
) {
    constructor(capacity: Int = MIN_CAPACITY) : this(CircularArray(capacity))

    fun push(value: T) = entries.addLast(value)

    fun popOrNull(): T? = try {
        entries.popLast()
    } catch (_: Exception) {
        null
    }

    fun peekOrNull(): T? = try {
        entries.last
    } catch (_: Exception) {
        null
    }

    fun peekNthOrNull(n: Int): T? = try {
        entries[(entries.size() - 1) - n ]
    } catch (_: Exception) {
        null
    }

    fun depth(): Int = entries.size()

    fun entries(): List<T> = List(entries.size()) { entries[it] }

    fun clear() = entries.clear()
}

private const val MIN_CAPACITY = 256
