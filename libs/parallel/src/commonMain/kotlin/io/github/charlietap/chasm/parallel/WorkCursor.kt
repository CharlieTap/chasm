@file:OptIn(ExperimentalAtomicApi::class)

package io.github.charlietap.chasm.parallel

import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.fetchAndIncrement

/** Hands out [count] pieces of work, each to whichever worker claims it first. */
class WorkCursor(
    private val count: Int,
) {

    private val next = AtomicInt(0)

    /** The next piece of work nobody has claimed, or [NOTHING_TO_CLAIM]. */
    fun claim(): Int {
        // an exhausted cursor never advances, so it cannot wrap around
        if (next.load() >= count) return NOTHING_TO_CLAIM

        val claimed = next.fetchAndIncrement()
        return if (claimed < count) claimed else NOTHING_TO_CLAIM
    }
}

const val NOTHING_TO_CLAIM = -1

inline fun ParallelTaskScope.claimEach(
    cursor: WorkCursor,
    action: (claimed: Int) -> Unit,
) {
    while (true) {
        ensureActive()
        val claimed = cursor.claim()
        if (claimed == NOTHING_TO_CLAIM) return
        action(claimed)
    }
}
