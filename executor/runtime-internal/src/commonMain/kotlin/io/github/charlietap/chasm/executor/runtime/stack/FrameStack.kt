package io.github.charlietap.chasm.executor.runtime.stack

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException

class FrameStack {

    private var elements: Array<ActivationFrame?> = arrayOfNulls(CAPACITY)
    private var top = 0

    fun push(value: ActivationFrame) = try {
        elements[top] = value
        top++
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.CallStackExhausted)
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.CallStackExhausted)
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

private const val CAPACITY = 1028
