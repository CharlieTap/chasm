package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.exception.HostRaisedWasmException

@InternalChasmApi
interface PendingExceptions {

    val hasPending: Boolean

    fun raise(exception: WasmException): Nothing

    fun publish(exception: WasmException)

    fun take(): WasmException
}

@InternalChasmApi
class PendingExceptionSlot : PendingExceptions {

    private var pending: WasmException? = null

    override val hasPending: Boolean
        get() = pending != null

    override fun raise(exception: WasmException): Nothing {

        pending = exception

        throw HostRaisedWasmException.instance
    }

    override fun publish(exception: WasmException) {
        pending = exception
    }

    override fun take(): WasmException {

        val exception = checkNotNull(pending) { "no exception is pending" }
        pending = null

        return exception
    }

    fun clear() {
        pending = null
    }
}
