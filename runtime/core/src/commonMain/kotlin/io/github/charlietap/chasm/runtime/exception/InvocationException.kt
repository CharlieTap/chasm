package io.github.charlietap.chasm.runtime.exception

import io.github.charlietap.chasm.runtime.error.InvocationError

class InvocationException(val error: InvocationError) : Exception() {

    /** Recorded only by the traceable dispatch loop used when trap diagnostics are enabled. */
    var faultIp: Int = UNKNOWN_FAULT_IP
        private set

    fun at(ip: Int): InvocationException {
        if (faultIp == UNKNOWN_FAULT_IP) faultIp = ip
        return this
    }

    companion object {
        const val UNKNOWN_FAULT_IP = -1
    }
}
