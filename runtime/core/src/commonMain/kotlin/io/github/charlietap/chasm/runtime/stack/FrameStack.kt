package io.github.charlietap.chasm.runtime.stack

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException

class FrameStack {

    private var elements: Array<ActivationFrame?> = arrayOfNulls(INITIAL_CAPACITY)
    private var top = 0

    fun push(value: ActivationFrame) {
        val capacity = elements.size
        if (top == capacity) {
            if (capacity == MAX_CAPACITY) {
                throw InvocationException(InvocationError.CallStackExhausted)
            }
            elements = elements.copyOf(minOf(capacity * 2, MAX_CAPACITY))
        }
        elements[top] = value
        top++
    }

    fun pop(): ActivationFrame = try {
        top--
        val value = elements[top]
        elements[top] = null
        value!!
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.MissingStackFrame)
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.MissingStackFrame)
    }

    fun peek(): ActivationFrame = try {
        elements[top - 1]!!
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.MissingStackFrame)
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.MissingStackFrame)
    }

    fun peekNth(n: Int): ActivationFrame = try {
        elements[top - 1 - n]!!
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.MissingStackFrame)
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.MissingStackFrame)
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
            add(elements[i]!!)
        }
    }
}

private const val INITIAL_CAPACITY = 32
private const val MAX_CAPACITY = 1028
