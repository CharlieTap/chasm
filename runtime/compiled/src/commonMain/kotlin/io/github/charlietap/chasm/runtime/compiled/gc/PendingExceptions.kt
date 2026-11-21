package io.github.charlietap.chasm.runtime.compiled.gc

interface PendingExceptions {

    fun raise(exception: WasmException): Nothing

    fun take(): WasmException
}
