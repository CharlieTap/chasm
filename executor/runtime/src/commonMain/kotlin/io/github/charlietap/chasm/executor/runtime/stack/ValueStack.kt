package io.github.charlietap.chasm.executor.runtime.stack

import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import kotlin.jvm.JvmOverloads

class ValueStack
    @JvmOverloads
    constructor(minCapacity: Int = MIN_CAPACITY) {

        private var elements: Array<ExecutionValue?>
        private var top = 0

        init {
            val arrayCapacity: Int =
                if (minCapacity.countOneBits() != 1) {
                    (minCapacity - 1).takeHighestOneBit() shl 1
                } else {
                    minCapacity
                }
            elements = arrayOfNulls<ExecutionValue?>(arrayCapacity)
        }

        fun push(value: ExecutionValue) {
            elements[top] = value
            top++
            if (top == elements.size) {
                doubleCapacity()
            }
        }

        fun pushAll(values: Array<ExecutionValue>) {
            val requiredSize = top + values.size
            while (requiredSize > elements.size) {
                doubleCapacity()
            }
            values.copyInto(elements, startIndex = 0, endIndex = values.size, destinationOffset = top)
            top += values.size
        }

        fun popOrNull(): ExecutionValue? = try {
            top--
            val value = elements[top]
            elements[top] = null
            value
        } catch (_: Exception) {
            null
        }

        fun peekOrNull(): ExecutionValue? = try {
            elements[top - 1]
        } catch (_: Exception) {
            null
        }

        fun peekNthOrNull(n: Int): ExecutionValue? = try {
            elements[top - 1 - n]
        } catch (_: Exception) {
            null
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
                add(elements[i] as ExecutionValue)
            }
        }

        private fun doubleCapacity() {
            val newCapacity = elements.size * 2
            elements = elements.copyOf(newCapacity)
        }
    }

private const val MIN_CAPACITY = 256
