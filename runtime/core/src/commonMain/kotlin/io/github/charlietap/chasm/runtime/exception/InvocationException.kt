package io.github.charlietap.chasm.runtime.exception

import io.github.charlietap.chasm.runtime.diagnostic.FailedMemoryAccess
import io.github.charlietap.chasm.runtime.error.InvocationError

class InvocationException(val error: InvocationError) : Exception() {

    /** Recorded only by the traceable dispatch loop used when trap diagnostics are enabled. */
    var faultIp: Int = UNKNOWN_FAULT_IP
        private set

    // Recorded by compiled code when a memory access fails, which the interpreter instead reads from its frame.
    var memoryAccess: FailedMemoryAccess? = null
        private set

    // Pairs of frame pointer and entry IP, innermost first, for each interpreter entry into compiled code the
    // exception passed through, so a trap can walk past compiled frames, which keep no activation headers.
    var compiledEntries: IntArray = NO_ENTRIES
        private set

    var compiledEntryCount: Int = 0
        private set

    fun at(ip: Int): InvocationException {
        if (faultIp == UNKNOWN_FAULT_IP) faultIp = ip
        return this
    }

    fun failedAccess(access: FailedMemoryAccess): InvocationException {
        if (memoryAccess == null) memoryAccess = access
        return this
    }

    fun leftCompiledEntry(fp: Int, entryIp: Int): InvocationException {
        if (compiledEntries.size < (compiledEntryCount + 1) * 2) {
            compiledEntries = compiledEntries.copyOf(maxOf(INITIAL_ENTRIES, compiledEntries.size * 2))
        }
        compiledEntries[compiledEntryCount * 2] = fp
        compiledEntries[compiledEntryCount * 2 + 1] = entryIp
        compiledEntryCount++
        return this
    }

    companion object {
        const val UNKNOWN_FAULT_IP = -1
        private val NO_ENTRIES = IntArray(0)
        private const val INITIAL_ENTRIES = 8
    }
}
