@file:OptIn(ExperimentalAtomicApi::class)

package io.github.charlietap.chasm.parallel

import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

/** The reported failure with the lowest position, whatever order failures are reported in. */
class EarliestFailure<E : Any> {

    private val earliest = AtomicReference<Failure<E>?>(null)

    val error: E?
        get() = earliest.load()?.error

    fun report(position: Int, error: E) {
        val failure = Failure(position, error)
        while (true) {
            val current = earliest.load()
            if (current != null && current.position <= position) return
            if (earliest.compareAndSet(current, failure)) return
        }
    }

    fun isBeyond(position: Int): Boolean {
        val current = earliest.load()
        return current != null && position > current.position
    }

    private class Failure<E>(
        val position: Int,
        val error: E,
    )
}
