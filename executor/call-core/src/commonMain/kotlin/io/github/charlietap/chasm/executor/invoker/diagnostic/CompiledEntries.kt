package io.github.charlietap.chasm.executor.invoker.diagnostic

import io.github.charlietap.chasm.runtime.exception.InvocationException

/**
 * The entries into compiled code a trap passed through, innermost first.
 * The walk only moves outward, so each search starts where the last stopped.
 */
internal class CompiledEntries(
    exception: Throwable?,
) {
    private val entries: IntArray
    private val count: Int
    private var cursor = 0

    init {
        val passed = exception as? InvocationException
        entries = passed?.compiledEntries ?: IntArray(0)
        count = passed?.compiledEntryCount ?: 0
    }

    /** The innermost entry when the trap happened in the compiled code it entered, or -1. */
    fun innermost(faultIp: Int): Int = if (count > 0 && entryIp(0) == faultIp) 0 else -1

    /** The innermost entry at or below [callerFp], or -1. */
    fun below(callerFp: Int): Int {
        while (cursor < count) {
            if (framePointer(cursor) <= callerFp) return cursor
            cursor++
        }
        return -1
    }

    fun framePointer(entry: Int): Int = entries[entry * 2]

    fun entryIp(entry: Int): Int = entries[entry * 2 + 1]
}
