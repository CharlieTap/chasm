package io.github.charlietap.chasm.executor.runtime.stack

import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import kotlin.jvm.JvmOverloads

class ValueStack
    @JvmOverloads
    constructor(minCapacity: Int = MIN_CAPACITY) : List<ExecutionValue> {

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

        override val size: Int
            get() = top

        override fun isEmpty(): Boolean = top == 0

        override fun get(index: Int): ExecutionValue = elements[index]
            ?: throw IndexOutOfBoundsException("Index: $index, Size: $top")

        override fun contains(element: ExecutionValue): Boolean = elements.take(top).contains(element)

        override fun containsAll(elements: Collection<ExecutionValue>): Boolean = elements.all { contains(it) }

        override fun iterator(): Iterator<ExecutionValue> = elements.take(top).map { it!! }.iterator()

        override fun indexOf(element: ExecutionValue): Int = elements.indexOfFirst { it == element }.takeIf { it < top } ?: -1

        override fun lastIndexOf(element: ExecutionValue): Int = elements.indexOfLast { it == element }.takeIf { it < top } ?: -1

        override fun listIterator(): ListIterator<ExecutionValue> = elements.take(top).map { it!! }.listIterator()

        override fun listIterator(index: Int): ListIterator<ExecutionValue> =
            elements.take(top).map { it!! }.listIterator(index)

        override fun subList(fromIndex: Int, toIndex: Int): List<ExecutionValue> {
            if (fromIndex < 0 || toIndex > top || fromIndex > toIndex) {
                throw IndexOutOfBoundsException("fromIndex: $fromIndex, toIndex: $toIndex, Size: $top")
            }
            return elements.slice(fromIndex until toIndex).map { it!! }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is List<*>) return false
            if (size != other.size) return false

            for (i in 0 until size) {
                if (this[i] != other[i]) return false
            }

            return true
        }

        override fun hashCode(): Int {
            return elements.take(top).map { it.hashCode() }.fold(1) { acc, hash -> 31 * acc + hash }
        }
    }

private const val MIN_CAPACITY = 256
