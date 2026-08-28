package io.github.charlietap.chasm.runtime.stack

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.runtime.exception.InvocationException

class ControlStack(
    initialHandlers: List<ExceptionHandler> = emptyList(),
) {

    private var handlers = arrayOfNulls<ExceptionHandler>(INITIAL_CAPACITY)
    private var depth = 0

    init {
        initialHandlers.forEach(this::push)
    }

    fun push(handler: ExceptionHandler) {
        handlers[depth] = handler
        depth++
        if (depth == handlers.size) {
            doubleCapacity()
        }
    }

    fun popHandler(): ExceptionHandler = try {
        depth--
        val handler = handlers[depth]
        handlers[depth] = null
        handler!!
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.UncaughtException)
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.UncaughtException)
    }

    fun handlersDepth(): Int = depth

    fun clear() {
        for (index in 0 until depth) {
            handlers[index] = null
        }
        depth = 0
    }

    private fun doubleCapacity() {
        handlers = handlers.copyOf(handlers.size * 2)
    }
}

private const val INITIAL_CAPACITY = 32
