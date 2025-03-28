package io.github.charlietap.chasm.runtime.stack

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import kotlin.jvm.JvmOverloads

class LabelStack
    @JvmOverloads
    constructor(minCapacity: Int = MIN_CAPACITY) {

        private var elements: Array<ControlStack.Entry.Label?>
        private var top = 0

        init {
            val arrayCapacity: Int =
                if (minCapacity.countOneBits() != 1) {
                    (minCapacity - 1).takeHighestOneBit() shl 1
                } else {
                    minCapacity
                }
            elements = arrayOfNulls<ControlStack.Entry.Label?>(arrayCapacity)
        }

        fun push(value: ControlStack.Entry.Label) {
            elements[top] = value
            top++
            if (top == elements.size) {
                doubleCapacity()
            }
        }

        fun pushAll(values: Array<ControlStack.Entry.Label>) {
            val requiredSize = top + values.size
            while (requiredSize > elements.size) {
                doubleCapacity()
            }
            values.copyInto(elements, startIndex = 0, endIndex = values.size, destinationOffset = top)
            top += values.size
        }

        fun pop(): ControlStack.Entry.Label = try {
            top--
            val value = elements[top]
            elements[top] = null
            value!!
        } catch (_: IndexOutOfBoundsException) {
            throw InvocationException(InvocationError.MissingStackLabel)
        } catch (_: IllegalArgumentException) {
            throw InvocationException(InvocationError.MissingStackLabel)
        }

        fun peek(): ControlStack.Entry.Label = try {
            elements[top - 1]!!
        } catch (_: IndexOutOfBoundsException) {
            throw InvocationException(InvocationError.MissingStackLabel)
        } catch (_: IllegalArgumentException) {
            throw InvocationException(InvocationError.MissingStackLabel)
        }

        fun peekNth(n: Int): ControlStack.Entry.Label = try {
            elements[top - 1 - n]!!
        } catch (_: IndexOutOfBoundsException) {
            throw InvocationException(InvocationError.MissingStackLabel)
        } catch (_: IllegalArgumentException) {
            throw InvocationException(InvocationError.MissingStackLabel)
        }

        fun shrink(depth: Int) {
            top = depth
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
                add(elements[i] as ControlStack.Entry.Label)
            }
        }

        private fun doubleCapacity() {
            val newCapacity = elements.size * 2
            elements = elements.copyOf(newCapacity)
        }
    }

private const val MIN_CAPACITY = 256
