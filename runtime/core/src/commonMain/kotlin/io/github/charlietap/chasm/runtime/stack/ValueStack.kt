package io.github.charlietap.chasm.runtime.stack

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException

class ValueStack(minCapacity: Int = MIN_CAPACITY) {

    var framePointer = 0
    private var elements: LongArray
    private var top = 0

    init {
        val arrayCapacity: Int =
            if (minCapacity.countOneBits() != 1) {
                (minCapacity - 1).takeHighestOneBit() shl 1
            } else {
                minCapacity
            }
        elements = LongArray(arrayCapacity)
    }

    fun getLocal(localIndex: Int): Long = elements[framePointer + localIndex]

    fun setLocal(
        localIndex: Int,
        value: Long,
    ) {
        elements[framePointer + localIndex] = value
    }

    fun peek(): Long = try {
        elements[top - 1]
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.MissingStackValue)
    }

    fun push(value: Long) = try {
        elements[top] = value
        top++
    } catch (_: IndexOutOfBoundsException) {
        doubleCapacity()
        elements[top] = value
        top++
    }

    fun pop(): Long = try {
        top--
        elements[top]
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.MissingStackValue)
    }

    fun peekI32(): Int = try {
        elements[top - 1].toInt()
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.MissingStackValue)
    }

    fun peekNthI32(n: Int): Int = try {
        elements[top - 1 - n].toInt()
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.MissingStackValue)
    }

    fun pushI32(value: Int) = try {
        elements[top] = value.toLong()
        top++
    } catch (_: IndexOutOfBoundsException) {
        doubleCapacity()
        elements[top] = value.toLong()
        top++
    }

    fun popI32(): Int = try {
        top--
        elements[top].toInt()
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.MissingStackValue)
    }

    fun peekI64(): Long = try {
        elements[top - 1]
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.MissingStackValue)
    }

    fun peekNthI64(n: Int): Long = try {
        elements[top - 1 - n]
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.MissingStackValue)
    }

    fun pushI64(value: Long) = try {
        elements[top] = value
        top++
    } catch (_: IndexOutOfBoundsException) {
        doubleCapacity()
        elements[top] = value
        top++
    }

    fun popI64(): Long = try {
        top--
        elements[top]
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.MissingStackValue)
    }

    fun pushF32(value: Float) {
        pushI32(value.toRawBits())
    }

    fun popF32(): Float = try {
        Float.fromBits(popI32())
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.MissingStackValue)
    }

    fun pushF64(value: Double) {
        pushI64(value.toRawBits())
    }

    fun popF64(): Double = try {
        Double.fromBits(popI64())
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.MissingStackValue)
    }

    fun push(
        values: LongArray,
    ) {
        val requiredSize = top + values.size
        while (requiredSize > elements.size) {
            doubleCapacity()
        }
        values.copyInto(elements, startIndex = 0, endIndex = values.size, destinationOffset = top)
        top += values.size
    }

    fun shrink(
        preserveTopN: Int,
        depth: Int,
    ) {
        elements.copyInto(
            destination = elements,
            destinationOffset = depth,
            startIndex = top - preserveTopN,
            endIndex = top,
        )
        top = depth + preserveTopN
    }

    fun depth(): Int = top

    fun clear() {
        elements.fill(0L)
        top = 0
    }

    fun doubleCapacity() {
        val newCapacity = elements.size * 2
        elements = elements.copyOf(newCapacity)
    }

    fun iterator(): Iterator<Long> = elements.slice(0..top - 1).iterator()

    override fun toString(): String {
        return buildString {
            append("[")
            for (i in 0 until top) {
                append(elements[i])
                if (i + 1 < top) append(", ")
            }
            append("]")
        }
    }

    companion object {
        private const val MIN_CAPACITY = 512
    }
}
