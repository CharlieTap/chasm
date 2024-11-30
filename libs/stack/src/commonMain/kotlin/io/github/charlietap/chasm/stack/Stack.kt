package io.github.charlietap.chasm.stack

import kotlin.jvm.JvmOverloads

class Stack<T>
    @JvmOverloads
    constructor(minCapacity: Int = MIN_CAPACITY) {

        private var elements: Array<T?>
        private var top = 0

        init {
            val arrayCapacity: Int =
                if (minCapacity.countOneBits() != 1) {
                    (minCapacity - 1).takeHighestOneBit() shl 1
                } else {
                    minCapacity
                }
            @Suppress("UNCHECKED_CAST")
            elements = arrayOfNulls<Any?>(arrayCapacity) as Array<T?>
        }

        fun push(value: T) {
            elements[top] = value
            top++
            if (top == elements.size) {
                doubleCapacity()
            }
        }

        fun popOrNull(): T? = try {
            top--
            val value = elements[top]
            elements[top] = null
            value
        } catch (_: Exception) {
            null
        }

        fun peekOrNull(): T? = try {
            elements[top - 1]
        } catch (_: Exception) {
            null
        }

        fun peekNthOrNull(n: Int): T? = try {
            elements[top - 1 - n]
        } catch (_: Exception) {
            null
        }

        fun depth(): Int = top

        fun clear() {
            for (i in 0 until top) {
                elements[i] = null
            }
            top = 0
        }

        fun entries() = buildList {
            for (i in 0 until top) {
                @Suppress("UNCHECKED_CAST")
                add(elements[i] as T)
            }
        }

        private fun doubleCapacity() {
            val newCapacity = elements.size * 2
            elements = elements.copyOf(newCapacity)
        }
    }

private const val MIN_CAPACITY = 256
