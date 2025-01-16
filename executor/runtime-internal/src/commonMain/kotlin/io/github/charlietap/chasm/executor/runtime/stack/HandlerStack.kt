package io.github.charlietap.chasm.executor.runtime.stack

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import kotlin.jvm.JvmOverloads

class HandlerStack
    @JvmOverloads
    constructor(minCapacity: Int = MIN_CAPACITY) {

        private var elements: Array<ExceptionHandler?>
        private var top = 0

        init {
            val arrayCapacity: Int =
                if (minCapacity.countOneBits() != 1) {
                    (minCapacity - 1).takeHighestOneBit() shl 1
                } else {
                    minCapacity
                }
            elements = arrayOfNulls<ExceptionHandler?>(arrayCapacity)
        }

        fun push(value: ExceptionHandler) {
            elements[top] = value
            top++
            if (top == elements.size) {
                doubleCapacity()
            }
        }

        fun pushAll(values: Array<ExceptionHandler>) {
            val requiredSize = top + values.size
            while (requiredSize > elements.size) {
                doubleCapacity()
            }
            values.copyInto(elements, startIndex = 0, endIndex = values.size, destinationOffset = top)
            top += values.size
        }

        fun pop(): ExceptionHandler = try {
            top--
            val value = elements[top]
            elements[top] = null
            value!!
        } catch (_: IndexOutOfBoundsException) {
            throw InvocationException(InvocationError.UncaughtException)
        } catch (_: IllegalArgumentException) {
            throw InvocationException(InvocationError.UncaughtException)
        }

        fun peek(): ExceptionHandler = try {
            elements[top - 1]!!
        } catch (_: IndexOutOfBoundsException) {
            throw InvocationException(InvocationError.UncaughtException)
        } catch (_: IllegalArgumentException) {
            throw InvocationException(InvocationError.UncaughtException)
        }

        fun peekNth(n: Int): ExceptionHandler = try {
            elements[top - 1 - n]!!
        } catch (_: IndexOutOfBoundsException) {
            throw InvocationException(InvocationError.UncaughtException)
        } catch (_: IllegalArgumentException) {
            throw InvocationException(InvocationError.UncaughtException)
        }

        fun shrink(preserveTopN: Int, depth: Int) {
            elements.copyInto(
                destination = elements,
                destinationOffset = depth,
                startIndex = top - preserveTopN,
                endIndex = top,
            )

            var i = depth + preserveTopN
            while (i < top) {
                elements[i] = null
                i++
            }

            top = depth + preserveTopN
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
                add(elements[i] as ExceptionHandler)
            }
        }

        private fun doubleCapacity() {
            val newCapacity = elements.size * 2
            elements = elements.copyOf(newCapacity)
        }
    }

private const val MIN_CAPACITY = 256
